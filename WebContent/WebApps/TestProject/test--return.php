<?php 

$CAL = __CHOICE__("CAL");

function hi() {
	global $CAL;
	if ($CAL)
		return "A";
	else
		return "B";
}

echo hi();

?>