registry=iad.ocir.io
ocir_username='<admin user>'
ocir_auth_token='<OCIR token>'
curl --user ${ocir_username}:${ocir_auth_token} -sSL "https://${registry}/20180419/docker/token"