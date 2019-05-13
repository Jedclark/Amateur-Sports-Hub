<?php
namespace App\Model\Table;

use Cake\ORM\Query;
use Cake\ORM\RulesChecker;
use Hayko\Mongodb\ORM\Table;
use Cake\Validation\Validator;
use Cake\Datasource\ConnectionManager;
/**
 * Basketball Model
 */
class SwimsTable extends Table
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
		$connection = ConnectionManager::get('mongodb');
        $this->setConnection($connection);
        $this->setTable('swimming_scores');
    }
	
}