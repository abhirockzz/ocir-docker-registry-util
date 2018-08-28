- `git clone https://github.com/abhirockzz/ocir-docker-registry-util.git`
- `cd ocir-docker-registry-util`
- `chmod +x ocir-get-v2-api-access-token.sh`
- get access token for invoking OCIR Docker V2 API
	- edit `ocir-get-v2-api-access-token.sh` to enter details - `registry`, `ocir_username` (admin level credentials) and `ocir_auth_token`
	- `./ocir-get-v2-api-access-token.sh`
	- from the JSON result, save the value for `token` attribute the result - it will be used in `util.properties` (next step)
- `cd ocir-image-cleanup`
- update `util.properties`

> Please enter the correct region in `ocir_registry_base` attribute

		ocir_registry_base=iad.ocir.io/20180419
		image_prefix_for_deletion=
		oci_tenancy_name=
		ocir_docker_v2_api_access_token=

- `mvn clean install`
- `java -jar target/ocir-util-1.0.0.jar util.properties`
