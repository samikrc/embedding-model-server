# embedding-model-server
Akka-http based microservice for serving embeddings for ML models

# Setting up
## Clone the repo and build the shaded jar package
- `git clone ...`

## Downloading model to the host
The USE 4.0 model is currently available at: https://tfhub.dev/google/universal-sentence-encoder/4
Follow the steps below to download and set up:
- Download the model: `wget https://tfhub.dev/google/universal-sentence-encoder/4?tf-hub-format=compressed`
- Uncompress the file to a folder names `use_4`: `tar -zxvf <downloaded file>`
- Note down the path of the `use_4` folder

## Downloading tensorflow library for JNI
- This code currently use TF1.x line. Get the link to download from this page: https://www.tensorflow.org/install/lang_java_legacy#download
- Create a folder `lib` within the repo root folder (`embedding-model-server/lib`).
- Download the binary and extract. Use `wget <file link>` and then `jar -cxvf <file name>`

## Set up the path
- Edit `embedding-model-server/src/local-config.conf`, to include the path to `use_4` folder set up above.

## Compile and run
- Compile with `mvn clean package -DskipTests`
- Run from the repo root folder with `java -Djava.library.path=./lib -jar target/embedding-model-server-1.0-SNAPSHOT.jar local src/local-config.conf`

