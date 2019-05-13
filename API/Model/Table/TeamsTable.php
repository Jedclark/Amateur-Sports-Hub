<?php
namespace App\Model\Table;

use Cake\ORM\Query;
use Cake\ORM\RulesChecker;
use Cake\ORM\Table;
use Cake\Validation\Validator;

class TeamsTable extends Table {
	
	public function initialize(array $config){
		parent::initialize($config);
		
		$this->setTable('teams');
		$this->setDisplayField('id');
		$this->setPrimaryKey('id');
		$this->hasOne("Users") -> setForeignKey("user_id");
		$this->belongsToMany("Users", [
			'joinTable' => 'teams_users'
		]);
		$this->belongsToMany("Seasons", [
			'joinTable' => 'seasons_teams'
		]);
	}
	
	
}