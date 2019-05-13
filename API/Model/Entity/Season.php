<?php
namespace App\Model\Entity;

use Cake\ORM\Entity;

/**
 * Competition Entity
 */
class Season extends Entity
{

    /**
     * Fields that can be mass assigned using newEntity() or patchEntity().
     *
     * Note that when '*' is set to true, this allows all unspecified fields to
     * be mass assigned. For security purposes, it is advised to set '*' to false
     * (or remove it), and explicitly make individual fields accessible as needed.
     *
     * @var array
     */
    protected $_accessible = [
        'competition_id' => true,
        'name' => true,
        'description' => true,
        'participants' => true,
        'is_active' => true,
		'fixtures_generated' => true
    ];

    /**
     * Fields that are excluded from JSON versions of the entity.
     *
     * @var array
     */
    protected $_hidden = [
	
    ];
}
