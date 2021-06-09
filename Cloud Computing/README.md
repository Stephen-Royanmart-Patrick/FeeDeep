# Cloud Computing
Cloud Computing Directory FeeDeep

- How to deploy ML use Flask and Nginx
- Manage Serverless APIs With API Gateway in GCP

# How to deploy ML on Flask or RESTful API

### _Install pip and Nginx (if using Python 3)_
```sh
sudo apt-get install python3-pip python3-dev nginx
```

### _Creating a Virtual Environment_

Next, create a virtual environment to isolate the Flask application from the main Python operating system.
Install virtualenv Python 3
```sh
sudo pip3 install virtualenv
```
### _Create a Flask project folder_
```sh
mkdir ~/belajar-flask
cd ~/belajar-flask	
```
### _If you want to use Python 3 on a virtualenv_
```sh
virtualenv -p python3 env
```
### _activate virtualenv_
```sh
source env/bin/activate
```
### _Installing Flask and Gunicorn_
```sh
pip install flask gunicorn
```
### _Creating a simple Flask application, displaying text_
```sh
nano aplikasi.py
```
content :
```sh
from flask import Flask
appku = Flask(__name__)

@appku.route("/")
def hello():
    return "<h1>Belajar Flask</h1>"

if __name__ == "__main__":
    appku.run(host='0.0.0.0')
```
### _Running FLask applications with development server_
```sh
python aplikasi.py
```
Browse http://localhost:5000.
If you succeed in displaying the word Learn Flask, proceed to the next stage.

### _Nginx Configuration_
The next step is to configure Nginx to communicate with Gunicorn via a proxy.
```sh
sudo nano /etc/nginx/sites-available/belajarflask
```
Content : 
```sh
server {
    listen 80;
    server_name NOMOR_IP_ATAU_DOMAIN;

    location / {
        include proxy_params;
        proxy_pass http://unix:/home/musa/belajar-flask/belajar-flask.sock;
    }
}
```
### _Activate the newly created virtual host/Nginx configuration block by linking to sites-enabled._
```sh
sudo ln -s /etc/nginx/sites-available/belajarflask /etc/nginx/sites-enabled
```
### _Testing for wrong Nginx configuration_
```sh 
sudo nginx -t
```
Lastly browsing http://IP_SERVER_OR_DOMAIN.

# Manage Serverless APIs With API Gateway in GCP
### _Create Cloud Function_
The code to get the data from the Firestore.
```sh
from flask import jsonify
from google.cloud import firestore


def get_<id>(request):
    try:
        if request.args and '<id>' in request.args:
            <id> = request.args.get('<id>')
        else:
            return 'Precondition Failed', 412

        client = firestore.Client()
        doc_ref = client.collection(u'<id>').document(u'{}'.format(<id>))
        doc = doc_ref.get()
        if doc.to_dict():
            response = jsonify(doc.to_dict())
            response.status_code = 200
        else:
            response = jsonify({
                'httpResponseCode': '404',
                'errorMessage': 'Employee does not exist'
            })
            response.status_code = 404
        return response
    except Exception as e:
        return e
```
Deploy the Cloud Function in private mode.
```sh
$ cd ..
$ gcloud functions deploy <id> --trigger-http \
  --runtime python37 --source cloud-function --entry-point get_emp \
  --region us-central1 --no-allow-unauthenticated
```
### _Deploy an API on API Gateway_

Enable required services
```sh
gcloud services enable apigateway.googleapis.com
gcloud services enable servicemanagement.googleapis.com
gcloud services enable servicecontrol.googleapis.com
```

Create an API
```sh
gcloud beta api-gateway apis create API_ID --project=$PROJECT_ID
```
Replace API_ID with your api_id e.g. my_api

### _Create an API Config_
Create an API config using OpenAPI spec.
```sh
# openapi-definition.yaml
swagger: "2.0"
info:
  title: my-api
  description: Serverless APIs with API Gateway
  version: 1.0.0
schemes:
 - https
produces:
 - application/json
paths:
#get collection from db
  /<collection>:
      get:
        summary: Get an <collection>
        operationId: get<collection>
        x-google-backend:
          address: #Replace with Cloud Function URL
          protocol: h2
        parameters:
          - name: <collection>_id
            in: query
            description: desc
            required: true
            type: string
        responses:
          '200':
            description: A successful response
            schema:
              type: string
```
Using following command, create an API config
```sh
gcloud beta api-gateway api-configs create CONFIG_ID \
  --api=API_ID --openapi-spec=API_DEFINITION \
  --project=$PROJECT_ID --backend-auth-service-account=SERVICE_ACCOUNT_EMAIL
```
You can use existing SERVICE_ACCOUNT_EMAIL or create new Service Account backend-auth-service by which API Gateway will call to backend service.

### _Enable API_
Get API name with hash using following command
```sh
gcloud beta api-gateway apis describe my-api --project=my-project
```
Then enable the API name from managedService field.
```sh
gcloud services enable API_ID-HASH.apigateway.PROJECT_ID.cloud.goog
```
### _Create a Gateway_
Now deploy the API config on a gateway. Deploying an API config on a gateway defines an external URL that API clients can use to access your API
```sh
gcloud beta api-gateway gateways create GATEWAY_ID \
  --api=API_ID --api-config=CONFIG_ID \
  --location=GCP_REGION --project=PROJECT_ID
```
On successful completion, use following command to view the details
```sh
gcloud beta api-gateway gateways describe GATEWAY_ID \
  --location=GCP_REGION --project=PROJECT_ID
```
This will give you defaultHostname: GATEWAY_ID-hash.uc.gateway.dev

Like for Cloud Run, and as described previously, a private Cloud Functions service can be reached by authenticated user with roles/cloudfunctions.invoker.
```sh
$   gcloud functions add-iam-policy-binding employee \
   --region us-central1 \
   --member "serviceAccount:$SERVICE_ACCOUNT_EMAIL" \
   --role "roles/cloudfunctions.invoker" \
   --project $PROJECT_ID
```
### _Redeploy_
Since the config has been changed, create a new API config with the modified OpenAPI spec using the following command.
```sh
gcloud beta api-gateway api-configs create NEW_CONFIG_ID \
--api=API_ID --openapi-spec=NEW_API_DEFINITION \
--project=PROJECT_ID --backend-auth-service-account=SERVICE_ACCOUNT_EMAIL
```
Update existing gateway with new API config.
```sh
gcloud beta api-gateway gateways update GATEWAY_ID \
  --api=API_ID --api-config=NEW_CONFIG_ID \
  --location=GCP_REGION --project=PROJECT_ID
```
Testing the API with API key.
```sh
curl https://gateway_id-<hash>-uc.gateway.dev/<collection>/11223344?key=API_KEY
```
