package edu.iis.mto.testreactor.dishwasher;

import static org.hamcrest.MatcherAssert.assertThat;

import edu.iis.mto.testreactor.dishwasher.engine.Engine;
import edu.iis.mto.testreactor.dishwasher.pump.WaterPump;
import org.hamcrest.Matchers;
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


}
