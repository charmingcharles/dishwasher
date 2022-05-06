package edu.iis.mto.testreactor.dishwasher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;

import edu.iis.mto.testreactor.dishwasher.engine.Engine;
import edu.iis.mto.testreactor.dishwasher.engine.EngineException;
import edu.iis.mto.testreactor.dishwasher.pump.PumpException;
import edu.iis.mto.testreactor.dishwasher.pump.WaterPump;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;


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
                .withStatus(status)
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

    @Test
    void dishWasherNotEnoughCapacityIncorrectRunTest(){
        Mockito.when(door.closed()).thenReturn(true);
        Mockito.when(dirtFilter.capacity()).thenReturn(40.0);
        ProgramConfiguration programConfiguration = generateProgramConfiguration(WashingProgram.ECO, FillLevel.HALF, true);
        RunResult expectedRunResult = generateRunResult(Status.ERROR_FILTER, 0);
        RunResult actualRunResult = dishWasher.start(programConfiguration);
        Assertions.assertEquals(expectedRunResult.getStatus(), actualRunResult.getStatus());
        Assertions.assertEquals(expectedRunResult.getRunMinutes(), actualRunResult.getRunMinutes());
    }

    @Test
    void dishWasherDoorsNotClosedIncorrectRunTest(){
        Mockito.when(door.closed()).thenReturn(false);
        Mockito.when(dirtFilter.capacity()).thenReturn(60.0);
        ProgramConfiguration programConfiguration = generateProgramConfiguration(WashingProgram.ECO, FillLevel.HALF, true);
        RunResult expectedRunResult = generateRunResult(Status.DOOR_OPEN, 0);
        RunResult actualRunResult = dishWasher.start(programConfiguration);
        Assertions.assertEquals(expectedRunResult.getStatus(), actualRunResult.getStatus());
        Assertions.assertEquals(expectedRunResult.getRunMinutes(), actualRunResult.getRunMinutes());
    }

    @Test
    void dishWasherPumpPourExceptionTest() throws PumpException {
        Mockito.when(door.closed()).thenReturn(true);
        Mockito.when(dirtFilter.capacity()).thenReturn(60.0);
        Mockito.doThrow(new PumpException()).when(waterPump).pour(FillLevel.HALF);
        ProgramConfiguration programConfiguration = generateProgramConfiguration(WashingProgram.ECO, FillLevel.HALF, true);
        RunResult expectedRunResult = generateRunResult(Status.ERROR_PUMP, 0);
        RunResult actualRunResult = dishWasher.start(programConfiguration);
        Assertions.assertEquals(expectedRunResult.getStatus(), actualRunResult.getStatus());
        Assertions.assertEquals(expectedRunResult.getRunMinutes(), actualRunResult.getRunMinutes());
    }

    @Test
    void dishWasherPumpDrainExceptionTest() throws PumpException {
        Mockito.when(door.closed()).thenReturn(true);
        Mockito.when(dirtFilter.capacity()).thenReturn(60.0);
        Mockito.doThrow(new PumpException()).when(waterPump).drain();
        ProgramConfiguration programConfiguration = generateProgramConfiguration(WashingProgram.ECO, FillLevel.HALF, true);
        RunResult expectedRunResult = generateRunResult(Status.ERROR_PUMP, 0);
        RunResult actualRunResult = dishWasher.start(programConfiguration);
        Assertions.assertEquals(expectedRunResult.getStatus(), actualRunResult.getStatus());
        Assertions.assertEquals(expectedRunResult.getRunMinutes(), actualRunResult.getRunMinutes());
    }

    @Test
    void dishWasherEngineExceptionTest() throws EngineException {
        ProgramConfiguration programConfiguration = generateProgramConfiguration(WashingProgram.ECO, FillLevel.HALF, true);
        RunResult expectedRunResult = generateRunResult(Status.ERROR_PROGRAM, 0);

        Mockito.when(door.closed()).thenReturn(true);
        Mockito.when(dirtFilter.capacity()).thenReturn(60.0);
        Mockito.doThrow(new EngineException()).when(engine).runProgram(List.of(programConfiguration.getProgram().ordinal(), programConfiguration.getProgram().getTimeInMinutes()));

        RunResult actualRunResult = dishWasher.start(programConfiguration);
        Assertions.assertEquals(expectedRunResult.getStatus(), actualRunResult.getStatus());
        Assertions.assertEquals(expectedRunResult.getRunMinutes(), actualRunResult.getRunMinutes());
    }

    @Test
    void correctRunBehaviourTest() throws PumpException, EngineException {
        Mockito.when(door.closed()).thenReturn(true);
        Mockito.when(dirtFilter.capacity()).thenReturn(60.0);
        ProgramConfiguration programConfiguration = generateProgramConfiguration(WashingProgram.ECO, FillLevel.HALF, true);
        dishWasher.start(programConfiguration);
        InOrder inOrder = Mockito.inOrder(door, dirtFilter, waterPump, engine);
        inOrder.verify(door).closed();
        inOrder.verify(dirtFilter).capacity();
        inOrder.verify(door).lock();
        inOrder.verify(waterPump).pour(any(FillLevel.class));
        inOrder.verify(engine).runProgram(any());
        inOrder.verify(waterPump).drain();
        inOrder.verify(door).unlock();
    }


}
