package testSuite;

import liquidjava.specification.StateRefinement;
import liquidjava.specification.StateSet;

@SuppressWarnings("unused")
@StateSet({"photoMode", "videoMode", "noMode"})
class CorrectEnumUsage {
	enum Mode {
		Photo, Video, Unknown
	}

	Mode mode;
	@StateRefinement(to="noMode(this)")
	public CorrectEnumUsage() {}

	@StateRefinement(from="noMode(this) && mode == Mode.Photo", to="photoMode(this)")
	@StateRefinement(from="noMode(this) && mode == Mode.Video", to="videoMode(this)")
	public void setMode(Mode mode) {
		this.mode = mode;
	}
	
	@StateRefinement(from="photoMode(this)")
	public void takePhoto() {}
	
	
	public static void main(String[] args) {
		// Correct
		CorrectEnumUsage st = new CorrectEnumUsage();
		st.setMode(Mode.Photo);
		st.takePhoto();
	}
}