# Notes for serving Tensorflow models

## Using "saved_model_cli" for validation

* Documentation available at: https://www.tensorflow.org/guide/saved_model#show_command

### Displaying information about USE v4:
```$xslt
samik@DESKTOP-8N43UKC:~$ saved_model_cli show --dir git/embedding-model-server/tfhub/use_4/
The given SavedModel contains the following tag-sets:
'serve'
```
```
samik@DESKTOP-8N43UKC:~$ saved_model_cli show --dir git/embedding-model-server/tfhub/use_4/ --tag_set serve
The given SavedModel MetaGraphDef contains SignatureDefs with the following keys:
SignatureDef key: "__saved_model_init_op"
SignatureDef key: "serving_default"
```
```
samik@DESKTOP-8N43UKC:~$ saved_model_cli show --dir git/embedding-model-server/tfhub/use_4/ --tag_set serve --signature_def serving_default
The given SavedModel SignatureDef contains the following input(s):
  inputs['inputs'] tensor_info:
      dtype: DT_STRING
      shape: unknown_rank
      name: serving_default_inputs:0
The given SavedModel SignatureDef contains the following output(s):
  outputs['output_0'] tensor_info:
      dtype: DT_FLOAT
      shape: (-1, 512)
      name: StatefulPartitionedCall_1:0
Method name is: tensorflow/serving/predict
```
### Running a prediction with USE v4
* Running with one input
```$xslt
samik@DESKTOP-8N43UKC:~$ saved_model_cli run --dir git/embedding-model-server/tfhub/use_4/ --tag_set serve --signature_def serving_default --input_exprs 'inputs=["what this is"]'
 ...
Result for output key output_0:
[[-0.05085026  0.03817649  0.01838036  0.0573011  -0.05769196  0.03091179
   0.04119135  0.028645    0.07060999  0.02564289  0.0530542   0.04041468
...
]]
```
* Running with multiple inputs
```$xslt
samik@DESKTOP-8N43UKC:~$ saved_model_cli run --dir git/embedding-model-server/tfhub/use_4/ --tag_set serve --signature_def serving_default --input_exprs 'inputs=["what this is", "how are you"]'
 ...
Result for output key output_0:
[[-0.05085025  0.03817652  0.01838035 ... -0.06827396 -0.01510744
   0.0516019 ]
 [-0.05739155 -0.01825172  0.05406101 ...  0.00544143 -0.00839537
   0.00212708]]
```
### Some references
* https://stackoverflow.com/questions/52715499/how-to-serve-a-tensorflow-module-specifically-universal-sentence-encoder
