# embedding-model-server
Akka-http based microservice for serving embeddings for ML models

# Setting up
## Downloading model to the host
The USE 4.0 model is currently available at: https://tfhub.dev/google/universal-sentence-encoder/4
Follow the steps below to download and set up:
- Download the model: `wget https://tfhub.dev/google/universal-sentence-encoder/4?tf-hub-format=compressed`
- Uncompress the file to a folder names `use_4`: `tar -zxvf <downloaded file>`
- Note down the path of the `use_4` folder

## Clone the repo and build the shaded jar package
- `git clone ...`
- `mvn package -DskipTests`

## Set up the path
- Edit 
