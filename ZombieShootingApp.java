import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Random;

public class ZombieShootingApp {
    private static JTextArea outputTextArea;
    private static HealthBar healthBar;
    private static Zombie zombie;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Zombie Shooting Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        zombie = new Zombie("Zombie");

        JPanel panel = new JPanel();
        JButton shootChestButton = createShootButton("Shoot Chest", DamageSystem.DamageableSection.Chest);
        JButton shootHeadButton = createShootButton("Shoot Head", DamageSystem.DamageableSection.Head);

        outputTextArea = createOutputTextArea(10, 30);
        healthBar = new HealthBar(100, 400, 30);

        panel.add(shootChestButton);
        panel.add(shootHeadButton);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(new JScrollPane(outputTextArea), BorderLayout.CENTER);
        frame.add(healthBar, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static JButton createShootButton(String buttonText, DamageSystem.DamageableSection section) {
        JButton button = new JButton(buttonText);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zombie.attack(section, DamageSystem.WeaponType.FireArm);
                updateDisplay();
            }
        });
        return button;
    }

    private static JTextArea createOutputTextArea(int rows, int columns) {
        JTextArea textArea = new JTextArea(rows, columns);
        textArea.setEditable(false);
        return textArea;
    }

    private static void updateDisplay() {
        StringBuilder displayText = new StringBuilder("Affected Sections and Blood Levels:\n");

        for (DamageSystem.DamageableSection section : DamageSystem.DamageableSection.values()) {
            DamageSystem.DamageEffect[] effects = zombie.getAppliedDamageEffects(section);
            int bloodLevel = zombie.getBloodLevel(section);

            if (effects[0] != null || bloodLevel > 0) {
                displayText.append("Section: ").append(section).append("\n");
                displayText.append("Applied Effects: ").append(Arrays.toString(effects)).append("\n");
                displayText.append("Blood Level: ").append(bloodLevel).append("\n\n");
            }
        }

        outputTextArea.setText(displayText.toString());

        int totalHealth = 100;
        int currentHealth = zombie.getOverallHealthPercentage();
        healthBar.update(currentHealth, totalHealth);
    }
}

class Creature {
    private DamageSystem damageSystem;

    public Creature(String name) {
        damageSystem = new DamageSystem();
    }

    public void attack(DamageSystem.DamageableSection section, DamageSystem.WeaponType weaponType) {
        damageSystem.applyDamage(section, weaponType);
    }

    public DamageSystem.DamageEffect[] getAppliedDamageEffects(DamageSystem.DamageableSection section) {
        return damageSystem.getAppliedDamageEffects(section);
    }

    public int getBloodLevel(DamageSystem.DamageableSection section) {
        return damageSystem.getBloodLevel(section);
    }
}

class Zombie extends Creature {
    public Zombie(String name) {
        super(name);
    }

    public int getOverallHealthPercentage() {
        int totalHealth = 100;
        int remainingHealth = totalHealth;

        for (DamageSystem.DamageableSection section : DamageSystem.DamageableSection.values()) {
            int bloodLevel = getBloodLevel(section);
            if (bloodLevel > 0) {
                remainingHealth -= bloodLevel;
            }
        }

        return Math.max(0, remainingHealth);
    }
}

class DamageSystem {
    private Random random = new Random();
    private DamageEffect[][] appliedDamageEffects = new DamageEffect[DamageableSection.values().length][3];
    private int[] bloodLevels = new int[DamageableSection.values().length];

    public enum DamageEffect {
        Death, LightBleeding, HeavyBleeding, Wound, Fracture, Break, Puncture, OrganDamage
    }

    public enum DamageableSection {
        Head, Chest, Stomach, Waist, LeftLeg, RightLeg, LeftArm, RightArm, Heart, LeftLung, RightLung, StomachOrgan, Intestines, Liver
    }

    public enum WeaponType {
        FireArm, Explosion
    }

    public void applyDamage(DamageableSection section, WeaponType weaponType) {
        DamageEffect[] effect = new DamageEffect[3];

        switch (section) {
            case Head:
                effect[0] = DamageEffect.Death;
                break;
            case Chest:
                effect[0] = DamageEffect.HeavyBleeding;
                effect[1] = DamageEffect.Wound;
                effect[2] = DamageEffect.Puncture;
                break;
            case Stomach:
                effect[0] = DamageEffect.HeavyBleeding;
                effect[1] = DamageEffect.Wound;
                break;
            case Waist:
                effect[0] = DamageEffect.HeavyBleeding;
                // Randomly determine Break or Fracture
                if (random.nextDouble() < 0.55) {
                    effect[1] = DamageEffect.Break;
                } else {
                    effect[1] = DamageEffect.Fracture;
                }
                effect[2] = DamageEffect.Wound;
                break;
            case LeftLeg:
            case RightLeg:
                effect[0] = DamageEffect.LightBleeding;
                // Randomly determine Break or Fracture
                if (random.nextDouble() < 0.6) {
                    effect[1] = DamageEffect.Break;
                } else {
                    effect[1] = DamageEffect.Fracture;
                }
                effect[2] = DamageEffect.Wound;
                break;
            case LeftArm:
            case RightArm:
                effect[0] = DamageEffect.LightBleeding;
                // Randomly determine Break or Fracture
                if (random.nextDouble() < 0.6) {
                    effect[1] = DamageEffect.Break;
                } else {
                    effect[1] = DamageEffect.Fracture;
                }
                effect[2] = DamageEffect.Wound;
                break;
            case Heart:
            case LeftLung:
            case RightLung:
            case StomachOrgan:
            case Intestines:
            case Liver:
                effect[0] = DamageEffect.OrganDamage;
                break;
        }

        applyDamageEffect(section, effect, weaponType);
    }

    private void applyDamageEffect(DamageableSection section, DamageEffect[] effect, WeaponType weaponType) {
        for (int i = 0; i < effect.length; i++) {
            if (effect[i] != null) {
                appliedDamageEffects[section.ordinal()][i] = effect[i];
                if (effect[i] == DamageEffect.HeavyBleeding || effect[i] == DamageEffect.LightBleeding) {
                    // Reduce blood level
                    bloodLevels[section.ordinal()] -= 10;
                }
            }
        }
    }

    public DamageEffect[] getAppliedDamageEffects(DamageableSection section) {
        return appliedDamageEffects[section.ordinal()];
    }

    public int getBloodLevel(DamageableSection section) {
        return bloodLevels[section.ordinal()];
    }

    public int getOverallHealthPercentage() {
        int totalHealth = 100;
        int remainingHealth = totalHealth;

        for (DamageableSection section : DamageableSection.values()) {
            int bloodLevel = getBloodLevel(section);
            if (bloodLevel > 0) {
                remainingHealth -= bloodLevel;
            }
        }

        return Math.max(0, remainingHealth);
    }
}

class HealthBar extends JPanel {
    private int value;
    private int maxValue;
    private int barWidth;
    private int barHeight;

    public HealthBar(int maxValue, int barWidth, int barHeight) {
        this.value = 0;
        this.maxValue = maxValue;
        this.barWidth = barWidth;
        this.barHeight = barHeight;
    }

    public void update(int value, int maxValue) {
        this.value = value;
        this.maxValue = maxValue;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = (int) ((double) value / maxValue * barWidth);
        g.setColor(Color.RED);
        g.fillRect(0, 0, width, barHeight);
    }
}
