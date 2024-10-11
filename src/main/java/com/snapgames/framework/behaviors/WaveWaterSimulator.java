package com.snapgames.framework.behaviors;

import com.snapgames.framework.entity.Entity;
import com.snapgames.framework.io.InputListener;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * The {@link WaveWaterSimulator} provides specific Behavior processing for a WorldArea to be used as Water.
 * <p>
 * It will interact with its own environment and with colliding objects.
 *
 * @author Frederic Delorme
 * @since 1.0.1
 */
public class WaveWaterSimulator<T extends Entity<T>> implements Behavior<T> {

    /**
     * Nb segments for the wave simulation
     */
    private int nbSegments = 100;
    /**
     * Facteur d'amortissement (ralentit les vagues)
     */
    private double damping = 0.99;
    /**
     * Tension du ressort entre les points
     */
    private double tension = 0.025;
    /**
     * Facteur de viscosité (pour lisser les vagues)
     */
    private double viscosity = 0.1;

    double points[];      // Positions des points sur l'axe Y (hauteur)
    double velocity[];     // Vitesse de chaque point


    public WaveWaterSimulator() {
        this(20, 0.998, 0.025, 0.2);
    }


    public WaveWaterSimulator(int nbSegments, double damping, double tension, double viscosity) {
        this.nbSegments = nbSegments;
        this.damping = damping;
        this.tension = tension;
        this.viscosity = viscosity;
        points = new double[nbSegments];       // Positions des points sur l'axe Y (hauteur)
        velocity = new double[nbSegments];
    }

    @Override
    public void input(InputListener il, T e) {
        if (il.isKeyPressed(KeyEvent.VK_W)) {
            onCollision(e, e);
        }
    }


    @Override
    public void start(T e) {
        for (int i = 0; i < nbSegments; i++) {
            points[i] = e.getHeight();  // La hauteur de base de l'eau (niveau normal)
            velocity[i] = 0.0f;      // Initialiser la vitesse à 0
        }
    }

    private void disturbWater(int index, double force) {
        if (index >= 0 && index < nbSegments) {
            velocity[index] += force;  // Ajoute une force au point sélectionné
        }
    }

    @Override
    public void onCollision(Entity<?> a, Entity<?> b) {
        if (b.getMass() > 10 && b.getMass() < 100) {
            double force = 0.05;
            int segmentWidth = (int) a.getWidth() / nbSegments;
            int index = (int) (b.getX() / segmentWidth);  // Trouve l'index correspondant à la position X du joueur
            disturbWater(index, force);          // Perturbe la surface à cet endroit
        }
    }

    @Override
    public void update(T e, double elapsed) {
        // Tableau temporaire pour stocker les nouvelles positions après mise à jour
        double[] newPoints = new double[nbSegments];
        for (int i = 0; i < nbSegments; i++) {
            newPoints[i] = points[i];  // Initialiser les nouvelles positions
        }

        // Mise à jour de chaque point en fonction de ses voisins
        for (int i = 1; i < nbSegments - 1; i++) {
            double leftDelta = points[i - 1] - points[i];  // Influence du voisin de gauche
            double rightDelta = points[i + 1] - points[i]; // Influence du voisin de droite

            // Mise à jour de la vitesse en tenant compte de la tension
            velocity[i] += tension * (leftDelta + rightDelta);
            velocity[i] *= damping;  // Amortissement (ralentit les vagues)

            // Ajout de l'effet de viscosité en moyennant les vitesses avec les voisins
            double viscosityEffect = viscosity * (velocity[i - 1] + velocity[i + 1] - 2 * velocity[i]);
            velocity[i] += viscosityEffect;

            // Mise à jour de la position Y en fonction de la nouvelle vitesse
            newPoints[i] += velocity[i];
        }

        // Mise à jour des positions des points après avoir calculé toutes les nouvelles valeurs
        System.arraycopy(newPoints, 0, points, 0, nbSegments);
    }

    @Override
    public void draw(Graphics2D g, T e) {
        int segmentWidth = (int) e.getWidth() / nbSegments;
        g.setColor(e.getColor());

        for (int i = 0; i < nbSegments - 1; i++) {
            // Dessiner une ligne entre chaque paire de points
            g.drawLine((int) e.getX() + i * segmentWidth, (int) (e.getY() + points[i] - e.getHeight()),
                    (int) e.getX() + (i + 1) * segmentWidth, (int) (e.getY() + points[i + 1] - e.getHeight()));
        }
    }
}
