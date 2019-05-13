<?php
namespace App\Model\Table;

use Cake\ORM\Query;
use Cake\ORM\RulesChecker;
use Cake\ORM\Table;
use Cake\Validation\Validator;

/**
 * Performances Model
 */
class PerformancesTable extends Table
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
        $this->setTable('performances');
        $this->setDisplayField('id');
        $this->setPrimaryKey('id');
		$this->hasOne("Season");
		$this->hasOne("Team");
    }
	
}
