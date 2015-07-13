<?php

function add_action($tag, $function_to_add) {
	global $wp_actions;

	$wp_actions[$tag][] = array('function' => $function_to_add);
}

function do_action($tag) {
	global $wp_actions; 

	foreach ( (array) $wp_actions[$tag] as $the_ ) {
		// Enter call_user_func_arrayt: create scope // Scope: COND1
		call_user_func_array($the_['function'], array()); // $the_['function'] = CHOICE (COND1, ..., ..)
		// Exit : COND1 && COND1
	}

}

?>