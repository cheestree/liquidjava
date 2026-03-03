package testingInProgress;

import liquidjava.specification.Refinement;

public class EnumRefinementMessage {
    enum Mode {
        Photo, Video, Unknown
    }

    public static void main(String[] args) {
        @Refinement("_==Mode.Photo")
        Mode test = Mode.Video;
        System.out.println(test);
    }
}
