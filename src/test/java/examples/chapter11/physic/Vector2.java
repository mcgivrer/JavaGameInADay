package examples.chapter11.physic;

public class Vector2 {
    // Attributs x et y du vecteur
    public double x;
    public double y;

    // Constructeurs
    public Vector2() {
        this.x = 0;
        this.y = 0;
    }

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Addition de vecteurs
    public Vector2 add(Vector2 v) {
        return new Vector2(this.x + v.x, this.y + v.y);
    }

    // Addition de vecteurs (opération in-place)
    public void addInPlace(Vector2 v) {
        this.x += v.x;
        this.y += v.y;
    }

    // Soustraction de vecteurs
    public Vector2 subtract(Vector2 v) {
        return new Vector2(this.x - v.x, this.y - v.y);
    }

    // Soustraction de vecteurs (opération in-place)
    public void subtractInPlace(Vector2 v) {
        this.x -= v.x;
        this.y -= v.y;
    }

    // Multiplication par un scalaire
    public Vector2 multiply(double scalar) {
        return new Vector2(this.x * scalar, this.y * scalar);
    }

    // Multiplication par un scalaire (opération in-place)
    public void multiplyInPlace(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
    }

    // Division par un scalaire
    public Vector2 divide(double scalar) {
        if (scalar != 0) {
            return new Vector2(this.x / scalar, this.y / scalar);
        } else {
            throw new ArithmeticException("Division by zero");
        }
    }

    // Division par un scalaire (opération in-place)
    public void divideInPlace(double scalar) {
        if (scalar != 0) {
            this.x /= scalar;
            this.y /= scalar;
        } else {
            throw new ArithmeticException("Division by zero");
        }
    }

    // Longueur (ou norme) du vecteur
    public double length() {
        return (double) Math.sqrt(this.x * this.x + this.y * this.y);
    }

    // Longueur au carré (utile pour éviter une racine carrée coûteuse)
    public double lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    // Normalisation du vecteur (opération in-place)
    public void normalize() {
        double len = length();
        if (len != 0) {
            this.x /= len;
            this.y /= len;
        }
    }

    // Normalisation du vecteur (retourne un nouveau vecteur normalisé)
    public Vector2 normalized() {
        double len = length();
        if (len != 0) {
            return new Vector2(this.x / len, this.y / len);
        }
        return new Vector2(0, 0);
    }

    // Produit scalaire (dot product)
    public double dot(Vector2 v) {
        return this.x * v.x + this.y * v.y;
    }

    // Produit vectoriel 2D (cross product en 2D donne un scalaire)
    public double cross(Vector2 v) {
        return this.x * v.y - this.y * v.x;
    }

    // Distance entre deux vecteurs
    public static double distance(Vector2 v1, Vector2 v2) {
        return (double) Math.sqrt(Math.pow(v1.x - v2.x, 2) + Math.pow(v1.y - v2.y, 2));
    }

    // Distance au carré (évite une racine carrée pour des comparaisons)
    public static double distanceSquared(Vector2 v1, Vector2 v2) {
        return (v1.x - v2.x) * (v1.x - v2.x) + (v1.y - v2.y) * (v1.y - v2.y);
    }

    // Utilitaire pour "clamp" les valeurs
    Vector2 clamp(double value) {
        x = Math.signum(x) * Math.min(Math.abs(x), value);
        y = Math.signum(y) * Math.min(Math.abs(y), value);
        return this;
    }


    // Affichage du vecteur sous forme de chaîne
    @Override
    public String toString() {
        return "Vector2(" + x + ", " + y + ")";
    }

    // Vérification d'égalité de deux vecteurs
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Vector2 vector2 = (Vector2) obj;
        return Double.compare(vector2.x, x) == 0 && Double.compare(vector2.y, y) == 0;
    }

    @Override
    public int hashCode() {
        long result = (x != +0.0 ? Double.doubleToLongBits(x) : 0);
        result = 31 * result + (y != +0.0 ? Double.doubleToLongBits(y) : 0);
        return (int) result;
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
