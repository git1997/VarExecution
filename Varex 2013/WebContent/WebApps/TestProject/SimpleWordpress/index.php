<?php

include "wp-includes/load.php";
include "wp-includes/plugin.php";

$wp_actions = array(); // Hooks

$content = "Default content ";
$footer = "Default footer";

// Load active plugins.
foreach ( wp_get_active_and_valid_plugins() as $plugin ) {
	include ( $plugin );
}

// Invoke call back functions.
do_action("footer_hook");

$html = $content . $footer;
echo $html;

$r1 = strpos($html, 'Google');
$r2 = $r1 !== false;
echo $r2;
// assertTrue($r2);


if (__INSTRUMENT__)
	__ASSERT__(strpos($html, 'Google') !== false); // CHOICE($FACEBOOK, true, CHOICE($GOOGLE, true, false))

// FINAL ERROR OUTPUT: assert failed -- if $FACEBOOK || !GOOGLE

?>