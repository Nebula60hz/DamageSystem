import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.List;

public class DamageSystem {
    private Random random = new Random();
    private Timer painTimer = new Timer();

    public enum DamageEffect {
        Death, LightBleeding, HeavyBleeding, Wound, Fracture, Break, Puncture, OrganDamage, ArmBreak
    }

    public enum DamageableSection {
        Head, Chest, Waist, LeftLeg, RightLeg, LeftArm, RightArm,
        Heart, LeftLung, RightLung, StomachOrgan, Intestines, Liver
    }

    public enum WeaponType {
        FireArm, Explosion
    }

    public enum PainEffect {
        SharpShootingPain, InternalDisruptions, NoPain
    }

    private int[] bloodLevels = new int[DamageableSection.values().length];
    private DamageEffect[][] appliedDamageEffects = new DamageEffect[DamageableSection.values().length][3];
    private PainEffect[] painEffects = new PainEffect[DamageableSection.values().length];
    private int[] painLevels = new int[DamageableSection.values().length];

    public DamageSystem() {
        // Initialize blood levels and pain levels for each section
        for (int i = 0; i < bloodLevels.length; i++) {
            bloodLevels[i] = 100;
            painLevels[i] = 1;
        }

        // Schedule a timer task to update pain levels periodically
        painTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updatePainLevels();
            }
        }, 0, 5000); // Update pain levels every 5 seconds
    }

    private void updatePainLevels() {
        for (int i = 0; i < painLevels.length; i++) {
            int currentPainLevel = painLevels[i];
            int fluctuation = random.nextInt(3) - 1; // Random fluctuation between -1 and 1
            int newPainLevel = Math.max(1, Math.min(10, currentPainLevel + fluctuation));
            painLevels[i] = newPainLevel;
        }
    }

    public void applyDamage(DamageableSection section, WeaponType weaponType) {
        DamageEffect[] effects = determineDamageEffects(section, weaponType);

        // Apply the damage effect to the character
        applyDamageEffect(effects, section);
        appliedDamageEffects[section.ordinal()] = effects;

        // Determine and store pain effects
        PainEffect painEffect = determinePainEffect(effects);
        painEffects[section.ordinal()] = painEffect;

        // Output affected sections
        outputAffectedSections();
    }

    private DamageEffect[] determineDamageEffects(DamageableSection section, WeaponType weaponType) {
        DamageEffect[] effects = new DamageEffect[3];

        switch (section) {
            case Head:
                effects[0] = DamageEffect.Death;
                break;
            case Chest:
                if (weaponType == WeaponType.FireArm) {
                    effects[0] = DamageEffect.Puncture;
                } else if (weaponType == WeaponType.Explosion) {
                    effects[0] = DamageEffect.Break;
                }
                effects[1] = DamageEffect.Wound;
                break;
            case Waist:
                if (weaponType == WeaponType.FireArm) {
                    effects[0] = DamageEffect.Puncture;
                } else if (weaponType == WeaponType.Explosion) {
                    effects[0] = DamageEffect.Break;
                }
                effects[1] = DamageEffect.Wound;
                break;
            default:
                // If the section is not explicitly defined, set all effects to null
                break;
        }

        if ((weaponType == WeaponType.FireArm || weaponType == WeaponType.Explosion) &&
                (section == DamageableSection.LeftLeg || section == DamageableSection.RightLeg)) {
            effects[0] = DamageEffect.Break;
        }

        if ((weaponType == WeaponType.FireArm || weaponType == WeaponType.Explosion) &&
                (section == DamageableSection.LeftArm || section == DamageableSection.RightArm)) {
            effects[0] = DamageEffect.ArmBreak;
        }

        return effects;
    }

    private PainEffect determinePainEffect(DamageEffect[] effects) {
        for (DamageEffect effect : effects) {
            if (effect != null) {
                switch (effect) {
                    case Fracture:
                    case ArmBreak:
                    case Break:
                        return PainEffect.SharpShootingPain;
                    case HeavyBleeding:
                    case OrganDamage:
                        return PainEffect.InternalDisruptions;
                }
            }
        }
        return PainEffect.NoPain;
    }

    private void applyDamageEffect(DamageEffect[] effects, DamageableSection section) {
        for (DamageEffect effect : effects) {
            if (effect != null) {
                switch (effect) {
                    case LightBleeding:
                        // Handle light bleeding effect
                        decreaseBloodLevel(section, 5);
                        break;
                    case HeavyBleeding:
                        // Handle heavy bleeding effect
                        decreaseBloodLevel(section, 10);
                        break;
                    case OrganDamage:
                        // Handle organ damage effect
                        decreaseBloodLevel(section, 15);
                        break;
                }
            }
        }
    }

    private void decreaseBloodLevel(DamageableSection section, int amount) {
        int currentBloodLevel = bloodLevels[section.ordinal()];
        bloodLevels[section.ordinal()] = Math.max(0, currentBloodLevel - amount); // Ensure blood level doesn't go below 0
    }

    private void outputAffectedSections() {
        List<DamageableSection> affectedSections = new ArrayList<>();
        for (DamageableSection section : DamageableSection.values()) {
            if (appliedDamageEffects[section.ordinal()][0] != null || painEffects[section.ordinal()] != PainEffect.NoPain) {
                affectedSections.add(section);
            }
        }

        System.out.println("Affected Sections:");
        for (DamageableSection section : affectedSections) {
            System.out.println("Section: " + section);
            System.out.print("Applied Effects: ");
            for (DamageEffect effect : appliedDamageEffects[section.ordinal()]) {
                if (effect != null) {
                    System.out.print(effect + ", ");
                }
            }
            System.out.println();
            System.out.println("Pain Effect: " + painEffects[section.ordinal()]);
            System.out.println();
        }
    }

    protected DamageEffect[] getAppliedDamageEffects(int section) {
        return appliedDamageEffects[section];
    }

     protected int getBloodLevel(int section) {
        return bloodLevels[section];
    }

    public static void main(String[] args) {
        DamageSystem damageSystem = new DamageSystem();
        damageSystem.applyDamage(DamageableSection.LeftArm, WeaponType.FireArm);
        damageSystem.applyDamage(DamageableSection.Chest, WeaponType.Explosion);
    }
}
