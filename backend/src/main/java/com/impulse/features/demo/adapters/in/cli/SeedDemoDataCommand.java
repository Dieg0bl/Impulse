package com.impulse.features.demo.adapters.in.cli;

import com.impulse.features.demo.application.usecase.SeedDemoDataUseCase;

/**
 * CLI adapter: SeedDemoDataCommand
 * Permite ejecutar el seed de datos demo desde línea de comandos (solo entorno seguro).
 */
public class SeedDemoDataCommand {
    private final SeedDemoDataUseCase useCase;

    public SeedDemoDataCommand(SeedDemoDataUseCase useCase) {
        this.useCase = useCase;
    }

    public void run() {
        useCase.execute();
        System.out.println("Datos demo/plantilla generados correctamente.");
    }
}
