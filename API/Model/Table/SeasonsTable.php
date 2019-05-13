<?php
namespace App\Model\Table;

use Cake\ORM\Query;
use Cake\ORM\RulesChecker;
use Cake\ORM\Table;
use Cake\Validation\Validator;

/**
 * Seasons Model
 */
class SeasonsTable extends Table
{

    /**
     * Initialize method
     *
     * @param array $config The configuration for the Table.
     * @return void
     */
    public function initialize(array $config)
    {
        parent::initialize($config);
        $this->setTable('seasons');
        $this->setDisplayField('id');
        $this->setPrimaryKey('id');
		$this->hasOne("Competition");
		$this->belongsToMany("Teams", [
			'joinTable' => 'seasons_teams'
		]);
		/*
		$this->hasMany("Teams") -> setForeignKey("user_id");
		$this->belongsToMany("Teams", [
			'joinTable' => 'teams_users'
		]); */
    }
	
}
