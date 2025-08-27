package com.impulse.onboarding;

import org.springframework.stereotype.Service;

@Service("onboardingQuickWinService")
public class QuickWinService {
    public record QuickWin(String title, String description, int minutes) {}
    public QuickWin assignFor(Long userId, String vertical){
        String v = vertical==null?"generic":vertical;
        return switch (v) {
            case "fitness" -> new QuickWin("Camina 5 minutos hoy","Un paso para arrancar",5);
            case "study" -> new QuickWin("Resumen de 1 página","Condensa un tema en 60 líneas",10);
            case "work" -> new QuickWin("Inbox a cero en 10'","Archiva/borra, sin excusas",10);
            default -> new QuickWin("Primer avance en 5'","Sube una evidencia simple",5);
        };
    }
}
