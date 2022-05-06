package edu.iis.mto.testreactor.dishwasher;

import static org.hamcrest.MatcherAssert.assertThat;

import edu.iis.mto.testreactor.dishwasher.engine.Engine;
import edu.iis.mto.testreactor.dishwasher.pump.WaterPump;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


class DishWasherTest {

    @Mock
    WaterPump waterPump;

    @Mock
    Engine engine;

    @Mock
    DirtFilter dirtFilter;

    @Mock
    Door door;

    DishWasher dishWasher;

    ProgramConfiguration generateProgramConfiguration(WashingProgram washingProgram, FillLevel fillLevel, boolean tabletsUsed){
        return ProgramConfiguration.builder()
                .withProgram(washingProgram)
                .withFillLevel(fillLevel)
                .withTabletsUsed(tabletsUsed)
                .build();
    }

    RunResult generateRunResult(Status status, int runMinutes){
        return RunResult.builder()
                .withStatus(Status.SUCCESS)
                .withRunMinutes(runMinutes)
                .build();
    }

    @BeforeEach
    void setUp(){
        waterPump = Mockito.mock(WaterPump.class);
        engine = Mockito.mock(Engine.class);
        dirtFilter = Mockito.mock(DirtFilter.class);
        door = Mockito.mock(Door.class);
        dishWasher = new DishWasher(waterPump, engine, dirtFilter, door);
    }

    @Test
    void itCompiles() {
        assertThat(true, Matchers.equalTo(true));
    }

    @Test
    void dishWasherCorrectRunTest(){
        Mockito.when(door.closed()).thenReturn(true);
        Mockito.when(dirtFilter.capacity()).thenReturn(60.0);
        ProgramConfiguration programConfiguration = generateProgramConfiguration(WashingProgram.ECO, FillLevel.HALF, true);
        RunResult expectedRunResult = generateRunResult(Status.SUCCESS, programConfiguration.getProgram().getTimeInMinutes());
        RunResult actualRunResult = dishWasher.start(programConfiguration);
        Assertions.assertEquals(expectedRunResult.getStatus(), actualRunResult.getStatus());
        Assertions.assertEquals(expectedRunResult.getRunMinutes(), actualRunResult.getRunMinutes());
    }


}
