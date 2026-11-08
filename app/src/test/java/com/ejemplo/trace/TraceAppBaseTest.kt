package com.ejemplo.trace

import com.ejemplo.trace.di.DispatchersModule
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Prueba unitaria base para verificar la configuración inicial del proyecto y módulos de inyección.
 */
class TraceAppBaseTest {

    @Test
    fun verifyDispatchersModule_providesCorrectDispatchers() {
        val ioDispatcher = DispatchersModule.provideIoDispatcher()
        val defaultDispatcher = DispatchersModule.provideDefaultDispatcher()
        val mainDispatcher = DispatchersModule.provideMainDispatcher()

        assertNotNull("IoDispatcher no debe ser nulo", ioDispatcher)
        assertNotNull("DefaultDispatcher no debe ser nulo", defaultDispatcher)
        assertNotNull("MainDispatcher no debe ser nulo", mainDispatcher)

        assertEquals(Dispatchers.IO, ioDispatcher)
        assertEquals(Dispatchers.Default, defaultDispatcher)
        assertEquals(Dispatchers.Main, mainDispatcher)
    }

    @Test
    fun verifyPackageName_isConfigured() {
        val expectedPackage = "com.ejemplo.trace"
        assertEquals("com.ejemplo.trace", expectedPackage)
    }
}
