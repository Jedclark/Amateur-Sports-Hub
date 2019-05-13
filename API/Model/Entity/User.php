<?php
namespace App\Model\Entity;

use Cake\ORM\Entity;

/**
 * User Entity
 *
 * @property int $id
 * @property int $first_name
 * @property int $last_name
 * @property string $username
 * @property string $password
 * @property string $email
 * @property \Cake\I18n\FrozenDate $date_created
 * @property \Cake\I18n\FrozenDate $last_modified
 */
class User extends Entity
{

    /**
     * Fields that can be mass assigned using newEntity() or patchEntity().
     */
    protected $_accessible = [
        'first_name' => true,
        'last_name' => true,
        'username' => true,
        'password' => true,
        'email' => true,
        'date_created' => true,
        'last_modified' => true,
        'token' => true,
    ];

    /**
     * Fields that are excluded from JSON versions of the entity.
     *
     * @var array
     */
    protected $_hidden = [
        'password',
		'_joinData'
    ];
}
