<?php

$foo = __CHOICE__("foo");
$bar = __CHOICE__("bar");

if ($foo)
	$cond = true;
else {
	if ($bar)
		$cond = true;
	else
		$cond = false;
}
echo $cond;

$cond = $foo || !$foo && $bar;
echo $cond;

if ($cond) // CHOICE($foo,1,CHOICE($bar, 1, 0))
	$x = "Good!";
else
	$x = "Bad!";

echo $x;

?>