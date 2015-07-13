<?php

$en = __CHOICE__("English", true, false);

$greetings = "Default greetings";

if (!$en) {
	$greetings = "Hallo Welt";
} 
else {
	$greetings = "Hello World";
}

$output = "Greetings: " . $greetings . " Welcome.";

echo $output;
?>