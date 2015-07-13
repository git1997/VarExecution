<?php
if ($COND1)
	$x = "Apple";
else
	$x = "Orange";

if ($COND2)
	$y = "p";
else
	$y = "e";

$result = strpos($x, $y);

echo $result;
?>