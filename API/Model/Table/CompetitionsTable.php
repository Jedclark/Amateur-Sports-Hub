<?php
namespace App\Model\Table;

use Cake\ORM\Query;
use Cake\ORM\RulesChecker;
use Cake\ORM\Table;
use Cake\Validation\Validator;

/**
 * Competitions Model
 */
class CompetitionsTable extends Table
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
        $this->setTable('competitions');
        $this->setDisplayField('id');
        $this->setPrimaryKey('id');
		$this->hasOne("User");
		$this->hasMany("Seasons");
		/*
		$this->hasMany("Teams") -> setForeignKey("user_id");
		$this->belongsToMany("Teams", [
			'joinTable' => 'teams_users'
		]); */
    }
	
}
