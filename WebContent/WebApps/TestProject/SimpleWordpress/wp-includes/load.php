<?php

/**
 * Returns array of plugin files to be included in global scope.
 *
 * The default directory is wp-content/plugins. To change the default directory
 * manually, define <code>WP_PLUGIN_DIR</code> and <code>WP_PLUGIN_URL</code>
 * in wp-config.php.
 *
 * @access private
 * @since 3.0.0
 * @return array Files to include
 */
function wp_get_active_and_valid_plugins() {
	$plugins = array();
	
	$active_plugins = array();
	
	if (__INSTRUMENT__) {
		$GOOGLE = __CHOICE__("GOOGLE");
		$FACEBOOK = __CHOICE__("FACEBOOK");
	}
	else {
		$GOOGLE = true;
		$FACEBOOK = true;
	}

	if ($GOOGLE) {
		$active_plugins[] = "google.php";
	}
	
	if ($FACEBOOK) {
		$active_plugins[] = "facebook.php";
	}

	foreach ( $active_plugins as $plugin ) {
		$plugins[] = "plugins" . '/' . $plugin;
	}
	return $plugins;
}

?>