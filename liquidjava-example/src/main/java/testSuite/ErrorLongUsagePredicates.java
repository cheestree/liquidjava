package testSuite;

import liquidjava.specification.Refinement;

public class ErrorLongUsagePredicates {
    void testLargeSubtractionWrong() {                                                                                                                    
        @Refinement("v - 9007199254740992 == 2")                                                                                                          
        long v = 9007199254740993L; // Refinement Error
    } 
    void testUUID(){
        @Refinement("((v/4096) % 16) == 2")
        long v = 0x01000000122341666L; // Refinement Error
    }
    
    void testWrongSign() {                                                                                                                                
      @Refinement("v < 0")                                                                                                                              
      long v = 42L; // Refinement Error
    }
}
