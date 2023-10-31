import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class ZombieShootingApp {
    private static JTextArea outputTextArea;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Zombie Shooting Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        Player player = new Player("Player");
        Zombie zombie = new Zombie("Zombie");

        JPanel panel = new JPanel();
        JButton shootChestButton = new JButton("Shoot Chest");
        JButton shootHeadButton = new JButton("Shoot Head");

        outputTextArea = new JTextArea(10, 30);
        outputTextArea.setEditable(false);

        shootChestButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                player.attack(DamageSystem.DamageableSection.Chest, DamageSystem.WeaponType.FireArm);
                updateDisplay(zombie);
            }
        });

        shootHeadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                player.attack(DamageSystem.DamageableSection.Head, DamageSystem.WeaponType.FireArm);
                updateDisplay(zombie);
            }
        });

        panel.add(shootChestButton);
        panel.add(shootHeadButton);
        frame.add(panel, BorderLayout.NORTH);
        frame.add(new JScrollPane(outputTextArea), BorderLayout.CENTER);

        frame.setVisible(true);
    }

    public static void updateDisplay(Zombie zombie) {
    StringBuilder displayText = new StringBuilder("Affected Sections and Blood Levels:\n");

    for (DamageSystem.DamageableSection section : DamageSystem.DamageableSection.values()) {
        DamageSystem.DamageEffect[] effects = zombie.getAppliedDamageEffects(section.ordinal());
        int bloodLevel = zombie.getBloodLevel(section.ordinal());

        if (effects[0] != null || effects[1] != null || effects[2] != null || bloodLevel > 0) {
            displayText.append("Section: ").append(section).append("\n");
            displayText.append("Applied Effects: ").append(Arrays.toString(effects)).append("\n");
            displayText.append("Blood Level: ").append(bloodLevel).append("\n\n");
        }
    }

    outputTextArea.setText(displayText.toString());
}

}

class Player extends Creature {
    public Player(String name) {
        super(name);
    }
}

class Zombie extends Creature {
    public Zombie(String name) {
        super(name);
    }

    protected DamageSystem.DamageEffect[] getAppliedDamageEffects(int section) {
        return super.getAppliedDamageEffects(section);
    }

    protected int getBloodLevel(int section) {
        return super.getBloodLevel(section);
    }

    @Override
    public void attack(DamageableSection section, WeaponType weaponType) {
        applyDamage(section, weaponType); // Call the applyDamage method
    }
}
