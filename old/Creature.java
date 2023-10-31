public class Creature extends DamageSystem {
    private String name;

    public Creature(String name) {
        this.name = name;
    }

    public void attack(DamageableSection section, WeaponType weaponType) {
        System.out.println(name + " attacks " + section + " with a " + weaponType);
        applyDamage(section, weaponType);
    }

    public static void main(String[] args) {
        Creature monster = new Creature("Monster");
        monster.attack(DamageableSection.LeftArm, WeaponType.FireArm);

        Creature player = new Creature("Player");
        player.attack(DamageableSection.Chest, WeaponType.Explosion);
    }
}
