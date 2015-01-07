<?php {
	
	function facebook_func() {
		global $footer;
		$footer = "Running Facebook plugin...";
	}
	
	add_action("footer_hook", facebook_func);

} ?>