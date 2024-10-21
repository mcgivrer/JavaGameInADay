public class MonProgramme2 {

    public MonProgramme2() {
        System.out.println("Démarrage de mon Programme2");
    }

    public void run(String[] args) {
        System.out.println("nb args:" + args.length);
        for (String arg : args) {
            System.out.println("argument : " + arg);
        }
    }

    public static void main(String[] args) {
        MonProgramme2 prog = new MonProgramme2();
        prog.run(args);
    }
}