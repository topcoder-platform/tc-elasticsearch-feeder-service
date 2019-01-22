/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.manager;

import com.appirio.service.challengefeeder.api.detail.BaseSubmissionData;
import com.appirio.service.challengefeeder.api.detail.ChallengeDetailData;
import com.appirio.service.challengefeeder.api.detail.DocumentData;
import com.appirio.service.challengefeeder.api.detail.RegistrantData;
import com.appirio.service.challengefeeder.api.detail.SubmissionData;
import com.appirio.service.challengefeeder.api.detail.SubmissionImage;
import com.appirio.service.challengefeeder.api.detail.TermsOfUseData;
import com.appirio.service.challengefeeder.api.detail.UserSubmissionData;
import com.appirio.service.challengefeeder.dao.ChallengeDetailFeederDAO;
import com.appirio.service.challengefeeder.dto.ChallengeFeederParam;
import com.appirio.service.challengefeeder.util.JestClientUtils;
import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.FilterParameter;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import io.searchbox.client.JestClient;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ChallengeDetailFeederManager is used to handle the challenge detail feeder.
 * 
 * Version 1.1 - Topcoder Elasticsearch Feeder Service - Jobs Cleanup And Improvement v1.0
 * - remove the userless dao
 * 
 *
 * @author TCSCODER
 * @version 1.1 
 */
public class ChallengeDetailFeederManager {

    /**
     * Logger used to log events
     */
    private static final Logger logger = LoggerFactory.getLogger(ChallengeDetailFeederManager.class);

    /**
     * contest submission type id
     */
    private static final Long CONTEST_SUBMISSION_TYPE_ID = 1L;

    /**
     * Checkpoint submission type id
     */
    private static final Long CHECKPOINT_SUBMISSION_TYPE_ID = 3L;

    /**
     * Round html header
     */
    private static final String ROUND_HEADER_HTML = "<h3 class='roundLabel'>Round %s</h3>";

    /**
     * DAO to access challenge data from the transactional database.
     */
    private final ChallengeDetailFeederDAO challengeDetailFeederDAO;

    /**
     * Studio submission image url template
     */
    @Setter
    private String submissionImageUrl;

    /**
     * The jestClient field
     */
    private final JestClient jestClient;

    /**
     * Create ChallengeDetailFeederManager
     *
     * @param jestClient the jestClient to use
     * @param challengeDetailFeederDAO the challengeFeederDAO to use
     */
    public ChallengeDetailFeederManager(JestClient jestClient, ChallengeDetailFeederDAO challengeDetailFeederDAO) {
        this.jestClient = jestClient;
        this.challengeDetailFeederDAO = challengeDetailFeederDAO;
    }

    /**
     * Push challenge feeder
     *
     * @param param the challenge feeders param to use
     * @throws SupplyException if any error occurs
     */
    public void pushChallengeFeeder(ChallengeFeederParam param) throws SupplyException {
        if (param.getType() == null || param.getType().trim().length() == 0) {
            param.setType("challenges");
        }
        if (param.getIndex() == null || param.getIndex().trim().length() == 0) {
            param.setIndex("challengesdetail");
        }

        if (param.getChallengeIds() == null || param.getChallengeIds().size() == 0) {
            throw new SupplyException("Challenge ids must be provided", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (param.getChallengeIds().contains(null)) {
            throw new SupplyException("Null challenge id is not allowed", HttpServletResponse.SC_BAD_REQUEST);
        }

        FilterParameter filter = new FilterParameter("challengeIds=in(" + ChallengeFeederUtil.listAsString(param.getChallengeIds()) + ")");
        QueryParameter queryParameter = new QueryParameter(new FieldSelector());
        queryParameter.setFilter(filter);
        List<ChallengeDetailData> challenges = this.challengeDetailFeederDAO.getChallenges(queryParameter);
        for (ChallengeDetailData challenge : challenges) {
            // fixing for trailing space caused by `case when` statement
            challenge.setTrack(challenge.getTrack());
        }

        List<Long> ids = challenges.stream().map(c -> c.getId()).collect(Collectors.toList());
        List<Long> idsNotFound = param.getChallengeIds().stream().filter(id -> !ids.contains(id)).collect(Collectors.toList());

        if (!idsNotFound.isEmpty()) {
            logger.warn("These challenge ids can not be found:" + idsNotFound);

            ids.removeAll(idsNotFound);
        }

        logger.info("aggregating challenge detail data for " + ids);

        if (challenges.size() == 0) {
            return;
        }

        for (ChallengeDetailData challenge : challenges) {
            String requirement = "";
            if ("DESIGN".equalsIgnoreCase(challenge.getTrack())) {
                if (challenge.getStudioDetailRequirements() != null)
                    requirement = challenge.getStudioDetailRequirements();
                if (challenge.getRound1Introduction() != null) {
                    if (!requirement.startsWith(challenge.getRound1Introduction())) {
                        requirement += String.format(ROUND_HEADER_HTML, "1") + challenge.getRound1Introduction();
                        requirement += "\n";
                    }
                }

                if (challenge.getRound2Introduction() != null) {
                    requirement += String.format(ROUND_HEADER_HTML, "2") + challenge.getRound2Introduction();
                }
            } else if ("DEVELOP_MARATHON_MATCH".equalsIgnoreCase(challenge.getSubTrack()) || "MARATHON_MATCH".equalsIgnoreCase(challenge.getSubTrack())) {
                if (challenge.getMarathonMatchDetailRequirements() != null) {
                    requirement = challenge.getMarathonMatchDetailRequirements();
                    requirement += "\n";
                }

                if (challenge.getMarathonMatchRules() != null) {
                    requirement += challenge.getMarathonMatchRules();
                }
            } else {
                if (challenge.getSoftwareDetailRequirements() != null)
                    requirement += challenge.getSoftwareDetailRequirements();
            }

            challenge.setDetailRequirements(requirement);
        }

        // associate all the data
        List<SubmissionData> submissions = this.challengeDetailFeederDAO.getSubmissions(queryParameter);
        this.associateAllSubmissions(challenges, submissions);

        List<RegistrantData> registrants = this.challengeDetailFeederDAO.getRegistrants(queryParameter);
        this.associateRegistrants(challenges, registrants);

        List<DocumentData> documents = this.challengeDetailFeederDAO.getDocuments(queryParameter);
        this.associateDocumets(challenges, documents);

        List<TermsOfUseData> terms = this.challengeDetailFeederDAO.getTerms(queryParameter);
        this.associateAllTermsOfUse(challenges, terms);

        try {
            JestClientUtils.pushFeeders(jestClient, param, challenges);
        } catch (IOException ioe) {
            SupplyException se = new SupplyException("Internal server error occurs", ioe);
            se.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw se;
        }
    }

    /**
     * Get changed challenge ids
     *
     * @param lastRunTimestamp the lastRunTimestamp to use
     * @return the List<TCID> result
     */
    public List<TCID> getChangedChallengeIds(Date lastRunTimestamp) {
        if (lastRunTimestamp == null) {
            throw new IllegalArgumentException("The lastRunTimestamp should be non-null.");
        }
        return this.challengeDetailFeederDAO.getChangedChallengeIds(lastRunTimestamp);
    }

    /**
     * Associate all terms of use
     *
     * @param challenges the challenges to use
     * @param termsOfUse the termsOfUse to use
     */
    private void associateAllTermsOfUse(List<ChallengeDetailData> challenges, List<TermsOfUseData> termsOfUse) {
        for (TermsOfUseData item : termsOfUse) {
            for (ChallengeDetailData challenge : challenges) {
                if (challenge.getId().equals(item.getChallengeId())) {
                    if (challenge.getTerms() == null) {
                        challenge.setTerms(new ArrayList<>());
                    }
                    challenge.getTerms().add(item);
                    break;
                }
            }
        }
    }

    /**
     * Associate registrants
     *
     * @param challenges the challenges to use
     * @param registrants the fileTypes to use
     */
    private void associateRegistrants(List<ChallengeDetailData> challenges, List<RegistrantData> registrants) {
        for (RegistrantData item : registrants) {
            item.setColorStyle(ChallengeFeederUtil.getColorStyle(item.getRating()));
            for (ChallengeDetailData challenge : challenges) {
                if (challenge.getId().equals(item.getChallengeId())) {
                    if (challenge.getRegistrants() == null) {
                        challenge.setRegistrants(new ArrayList<>());
                    }
                    challenge.getRegistrants().add(item);
                    break;
                }
            }
        }
    }

    /**
     * Associate all submissions
     *
     * @param challenges the challenges to use
     * @param submissions the submissions to use
     */
    private void associateAllSubmissions(List<ChallengeDetailData> challenges, List<SubmissionData> submissions) {
        Map<Long, Map<Long, List<SubmissionData>>> challengeSubmissions = submissions.stream()
                .filter(c -> CONTEST_SUBMISSION_TYPE_ID.equals(c.getSubmissionTypeId()))
                .collect(Collectors.groupingBy(SubmissionData::getChallengeId,
                            Collectors.groupingBy(SubmissionData::getSubmitterId)));

        Map<Long, List<BaseSubmissionData>> checkpointSubmissions = submissions.stream()
                .filter(c -> CHECKPOINT_SUBMISSION_TYPE_ID.equals(c.getSubmissionTypeId()))
                .map(c -> new BaseSubmissionData(c.getChallengeId(), c.getSubmissionId(), c.getSubmitter(), c.getSubmissionTime()))
                .collect(Collectors.groupingBy(BaseSubmissionData::getChallengeId));

        challenges.forEach(c -> {
            if (challengeSubmissions.get(c.getId()) != null) {
                List<UserSubmissionData> userSubmissions = new ArrayList<>();
                for (Map.Entry<Long, List<SubmissionData>> entry : challengeSubmissions.get(c.getId()).entrySet()) {
                    UserSubmissionData userSubmissionData = new UserSubmissionData();
                    userSubmissionData.setSubmitterId(entry.getKey());
                    userSubmissionData.setSubmitter(entry.getValue().get(0).getSubmitter());
                    if ("DESIGN".equalsIgnoreCase(c.getTrack()))
                        entry.getValue().forEach(s -> s.setSubmissionImage(
                                generateSubmissionImageUrls(s.getSubmissionId())));

                    userSubmissionData.setSubmissions(entry.getValue());
                    userSubmissions.add(userSubmissionData);
                }
                c.setSubmissions(userSubmissions);
            }
            if (checkpointSubmissions.get(c.getId()) != null) {
                c.setCheckpoints(checkpointSubmissions.get(c.getId()));
            }
        });
    }

    /**
     * Associate all documents
     *
     * @param challenges the challenges to use
     * @param documents the reviews to use
     */
    private void associateDocumets(List<ChallengeDetailData> challenges, List<DocumentData> documents) {
        for (DocumentData item : documents) {
            for (ChallengeDetailData challenge : challenges) {
                if (challenge.getId().equals(item.getChallengeId())) {
                    if (challenge.getDocuments() == null) {
                        challenge.setDocuments(new ArrayList<>());
                    }
                    challenge.getDocuments().add(item);
                    break;
                }
            }
        }
    }

    /**
     * Generate studio submissionImage
     *
     * @param submissionId studio submission id
     * @return submissionImage
     */
    private SubmissionImage generateSubmissionImageUrls(Long submissionId) {
        if (submissionImageUrl == null) return null;
        String templateUrl = String.format(submissionImageUrl, submissionId);

        String tinyImage = String.format("%s&sbt=tiny", templateUrl);
        String smallImage = String.format("%s&sbt=small", templateUrl);
        String mediumImage = String.format("%s&sbt=medium", templateUrl);
        String fullImage = String.format("%s&sbt=full", templateUrl);
        String thumbImage = String.format("%s&sbt=thumb", templateUrl);

        SubmissionImage submissionImage = new SubmissionImage();
        submissionImage.setPreviewPackage(templateUrl);
        submissionImage.setTiny(tinyImage);
        submissionImage.setSmall(smallImage);
        submissionImage.setMedium(mediumImage);
        submissionImage.setThumb(thumbImage);
        submissionImage.setFull(fullImage);

        return submissionImage;
    }
}
