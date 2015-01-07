<?php

$COND1 = __CHOICE__("COND1");

class Store {
	var $item = 3;
}

if ($COND1) {
	$x = new Store();
	$x->item = 'A';
}
else
	$x = new Store();

echo $x;

function put($obj, $value) {
	$obj->item = $value;
}


$a = new Store();

$a->item = 2;

if ($C1)
	put($a, 4);

$x = $a->item;

echo $x;

?>