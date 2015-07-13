<?php

function hello($x, $y) {
	return $x;
}

if ($COND1)
	$x = "Apple";
else
	$x = "Orange";

$result = hello($x, "test");

echo $result;

?>