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
import com.appirio.service.challengefeeder.dao.ChallengeDetailFeederDAO;
import com.appirio.service.challengefeeder.dto.ChallengeFeederParam;
import com.appirio.service.challengefeeder.util.JestClientUtils;
import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.FilterParameter;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import io.searchbox.client.JestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Setter;

/**
 * ChallengeDetailFeederManager is used to handle the challenge detail feeder.
 *
 * @author TCSCODER
 * @version 1.0
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
     * Studio type id
     */
    private static final Long STUDIO_TYPE_ID = 3L;

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
            throw new SupplyException("The index should be non-null and non-empty string.", HttpServletResponse.SC_BAD_REQUEST);
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

        List<Long> ids = challenges.stream().map(c -> c.getId()).collect(Collectors.toList());
        List<Long> idsNotFound = param.getChallengeIds().stream().filter(id -> !ids.contains(id)).collect(Collectors.toList());

        if (!idsNotFound.isEmpty()) {
            logger.warn("These challenge ids can not be found:" + idsNotFound);
        }

        logger.info("aggregating challenge detail data for " + param.getChallengeIds());

        for (ChallengeDetailData challenge : challenges) {
            String requirement = "";
            if (STUDIO_TYPE_ID.equals(challenge.getType())) {
                if (challenge.getStudioDetailRequirements() != null) requirement = challenge.getStudioDetailRequirements();
                if (challenge.getRound1Introduction() != null) {
                    if (!requirement.startsWith(challenge.getRound1Introduction())) {
                        requirement += challenge.getRound1Introduction();
                        requirement += "\n";
                    }
                }

                if (challenge.getRound2Introduction() != null) requirement += challenge.getRound2Introduction();
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

        logger.info("pushing challenge detail data to elasticsearch for " + param.getChallengeIds());

        try {
            JestClientUtils.pushFeeders(jestClient, param, challenges);
        } catch (IOException ioe) {
            SupplyException se = new SupplyException("Internal server error occurs", ioe);
            se.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw se;
        }
    }

    /**
     * Get timestamp from the persistence
     *
     * @throws SupplyException if any error occurs
     * @return the Date result
     */
    public Date getTimestamp() throws SupplyException {
        return this.challengeDetailFeederDAO.getTimestamp().getDate();
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
        for (SubmissionData item : submissions) {
            for (ChallengeDetailData challenge : challenges) {
                if (challenge.getId().equals(item.getChallengeId())) {
                    if (CONTEST_SUBMISSION_TYPE_ID.equals(item.getSubmissionTypeId())) {
                        if (challenge.getSubmissions() == null) {
                            challenge.setSubmissions(new ArrayList<>());
                        }
                        if (STUDIO_TYPE_ID.equals(challenge.getType()))
                            item.setSubmissionImage(generateSubmissionImageUrls(item.getSubmissionId()));
                        challenge.getSubmissions().add(item);
                    } else {
                        if (challenge.getCheckpoints() == null) {
                            challenge.setCheckpoints(new ArrayList<>());
                        }
                        BaseSubmissionData checkpoint = new BaseSubmissionData();
                        checkpoint.setSubmissionId(item.getSubmissionId());
                        checkpoint.setSubmissionTime(item.getSubmissionTime());
                        checkpoint.setSubmitter(item.getSubmitter());
                        challenge.getCheckpoints().add(checkpoint);
                    }
                }
            }
        }
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
