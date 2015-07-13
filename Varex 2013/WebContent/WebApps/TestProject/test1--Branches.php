<?php
	$GOOGLE = __CHOICE__("GOOGLE");
	$FACEBOOK = __CHOICE__("FACEBOOK");
	
	$x = "error.php";
	if ($GOOGLE) {
		$x = "google.php";
	}
	if ($FACEBOOK) {
		$x = "facebook.php"
	}
	$html = $x;
	echo $x;
?>