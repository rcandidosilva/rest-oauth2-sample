rest-oauth2-sample
==================

### Presentation Slides
http://www.slideshare.net/rcandidosilva/javaone-2014-securing-restful-resources-with-oauth2


# Authorization Grant
http://localhost:8080/conference/oauth/authorize?client_id=client&response_type=code&redirect_uri=http://localhost:8080/conference
http://localhost:8080/conference/oauth/token?grant_type=authorization_code&code=jqRr68&client_id=client&client_secret=secret&redirect_uri=http://localhost:8080/conference

# Password
http://localhost:8080/conference/oauth/token?grant_type=password&client_id=client2&client_secret=secret&username=admin&password=admin

# Client Credentials
http://localhost:8080/conference/oauth/token?grant_type=client_credentials&client_id=client3&client_secret=secret

# Implicit
http://localhost:8080/conference/oauth/authorize?response_type=token&client_id=client4&client_secret=secret&redirect_uri=http://localhost:8080/conference
