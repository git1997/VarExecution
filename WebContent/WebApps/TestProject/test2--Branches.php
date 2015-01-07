<?php

$lang = __CHOICE__("lang");

$greetings = "Default greetings";

if ($lang) {
	$greetings = "Hello World!";
} 
else {
	$greetings = "Hallo Welt!";
}

$output = "Greetings: " . $greetings . " Welcome.";

echo $output;
?>