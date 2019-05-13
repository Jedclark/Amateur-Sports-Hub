<?php
namespace App\Model\Entity;

use Cake\ORM\Entity;

class Team extends Entity {
	
	protected $_accessible = [
		'user_id' => true,
		'sport' => true,
		'team_name' => true,
		'nick_name' => true,
		'description' => true,
		'year_created' => true,
		'address' => true,
		'town' => true,
		'post_code' => true,
		'instructions' => true,
	];
	
	protected $_hidden = [
		'_joinData'
	];
	
}
