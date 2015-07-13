<?php
	if ($COND1)
		$file = "facebook.php";
	else
		$file = "google.php";
	
	require_once($file);
	include $file;
	
	echo $content;
?>
		