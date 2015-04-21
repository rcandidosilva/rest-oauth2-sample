<%@ taglib prefix="authz"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Conference</title>
<link type="text/css" rel="stylesheet"
	href="webjars/bootstrap/3.0.3/css/bootstrap.min.css" />
<script type="text/javascript" src="webjars/jquery/1.9.0/jquery.min.js"></script>
<script type="text/javascript"
	src="webjars/bootstrap/3.0.3/js/bootstrap.min.js"></script>

<authz:authorize ifAllGranted="USER">
	<script type='text/javascript'>
		function pictureDisplay(json) {
			for (var i = 0; i < json.photos.length; i++) {
				var photo = json.photos[i];
				document
						.write('<img src="photos/' + photo.id + '" alt="' + photo.name + '">');
			}
		}
	</script>
</authz:authorize>
</head>
<body>

	<div class="container">

		<h1>Conference Server</h1>

		<authz:authorize ifAllGranted="USER">
			<div class="form-horizontal">
				<a href="${pageContext.request.contextPath}/j_spring_security_logout"> Logout</a>
			</div>
		</authz:authorize>

	</div>

</body>
</html>
