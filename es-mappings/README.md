# How To Apply Custom Mappings
In order to apply the custom mappings for index. You need to create before documents inserted.

1. If the indexes already exists, you need to drop the index, like 
```
curl -XDELETE http://localhost:9200/<<index_name>>
```

2. Apply the custom mappings
```
curl -XPUT http://localhost:9200/<<index_name>> -d @chalenges.json
```
