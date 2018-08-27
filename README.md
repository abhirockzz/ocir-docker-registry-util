- Pre-requisite - Get `jq` using `brew install jq`
- `git clone https://github.com/abhirockzz/ocir-docker-registry-util.git`
- `cd ocir-docker-registry-util`
- `chmod +x ocir-get-v2-api-access-token.sh`
- get access token for invoking OCIR Docker V2 API
	- edit `ocir-get-v2-api-access-token.sh` to enter details - `registry`, `ocir_username` and `ocir_auth_token`
	- `./ocir-get-v2-api-access-token.sh`
	- save the result and copy it in `util.properties` (next step)
- `cd ocir-image-cleanup`
- update `util.properties`

> Please enter the correct region in `ocir_registry` attribute

		ocir_registry=iad.ocir.io
		image_prefix_for_deletion=e.g. workshop1>
		ocir_username=odx-jafar/abhishek.af.gupta@oracle.com
		ocir_docker_v2_api_access_token=<access token from above step>

- `mvn clean install`
- `java -jar target/ocir-util-1.0.0.jar util.properties`
