<?php 

if ($GOOGLE) {
	$plugins[] = "google.php";
}

print_r($plugins);

if ($FACEBOOK) {
	$plugins[] = "facebook.php";
}

print_r($plugins);

$content = "Default content";

foreach ($plugins as $plugin) {
	include $plugin;
}

$html = "Started. "  . $content;
echo $html;

?>