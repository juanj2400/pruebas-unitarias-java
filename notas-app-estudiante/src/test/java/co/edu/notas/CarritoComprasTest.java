
package co.edu.notas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class CarritoComprasTest {

    private CarritoCompras carrito;

    @BeforeEach
    void setUp() {
        carrito = new CarritoCompras();
    }

    // ---------- R2: subtotal ----------
    @Test
    @DisplayName("Subtotal = precio × cantidad de cada producto")
    void calcularSubtotal_variosProductos_sumaCorrecta() {
        carrito.agregarProducto("Cuaderno", 8_000, 3);
        carrito.agregarProducto("Esfero", 2_000, 5);
        assertEquals(34_000, carrito.calcularSubtotal(), 0.001);
    }

    @Test
    @DisplayName("Carrito vacío: subtotal 0")
    void calcularSubtotal_carritoVacio_retornaCero() {
        assertEquals(0, carrito.calcularSubtotal(), 0.001);
    }

    @Test
    @DisplayName("Carrito vacío: total 0")
    void calcularTotal_carritoVacio_retornaCero() {
        assertEquals(0, carrito.calcularTotal(), 0.001);
    }

    // ---------- R1: validaciones ----------
    @Test
    @DisplayName("Nombre nulo lanza excepción")
    void agregarProducto_nombreNulo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> carrito.agregarProducto(null, 1_000, 1));
    }

    @Test
    @DisplayName("Nombre vacío o en blanco lanza excepción")
    void agregarProducto_nombreVacio_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> carrito.agregarProducto("", 1_000, 1));
        assertThrows(IllegalArgumentException.class, () -> carrito.agregarProducto("   ", 1_000, 1));
    }

    @ParameterizedTest
    @ValueSource(doubles = { 0, -1 })
    @DisplayName("Precio cero o negativo lanza excepción")
    void agregarProducto_precioNoPositivo_lanzaExcepcion(double precio) {
        assertThrows(IllegalArgumentException.class, () -> carrito.agregarProducto("Lápiz", precio, 1));
    }

    @Test
    @DisplayName("Cantidad 0 lanza excepción (R1: cantidad > 0)")
    void agregarProducto_cantidadCero_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> carrito.agregarProducto("Lápiz", 1_000, 0));
    }

    @Test
    @DisplayName("Cantidad negativa lanza excepción")
    void agregarProducto_cantidadNegativa_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> carrito.agregarProducto("Lápiz", 1_000, -1));
    }

    // ---------- R3: SENA10 ----------
    @Test
    @DisplayName("SENA10 descuenta 10 % del subtotal")
    void calcularDescuento_cuponSena10_diezPorCiento() {
        carrito.agregarProducto("Libro", 100_000, 1);
        carrito.aplicarCupon("SENA10");
        assertEquals(10_000, carrito.calcularDescuento(), 0.001);
    }

    // ---------- R4: BIENVENIDO ----------
    @Test
    @DisplayName("BIENVENIDO con subtotal exacto de 50.000 descuenta 5.000")
    void calcularDescuento_bienvenidoEnFrontera_descuentaCincoMil() {
        carrito.agregarProducto("Kit", 50_000, 1);
        carrito.aplicarCupon("BIENVENIDO");
        assertEquals(5_000, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("BIENVENIDO con subtotal 49.999 no descuenta")
    void calcularDescuento_bienvenidoBajoFrontera_noDescuenta() {
        carrito.agregarProducto("Kit", 49_999, 1);
        carrito.aplicarCupon("BIENVENIDO");
        assertEquals(0, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("Sin cupón no hay descuento")
    void calcularDescuento_sinCupon_retornaCero() {
        carrito.agregarProducto("Kit", 80_000, 1);
        assertEquals(0, carrito.calcularDescuento(), 0.001);
    }

    // ---------- R5: cupón inválido ----------
    @Test
    @DisplayName("Cupón desconocido lanza excepción")
    void aplicarCupon_codigoInvalido_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> carrito.aplicarCupon("FALSO50"));
    }

    @Test
    @DisplayName("Cupón nulo lanza excepción")
    void aplicarCupon_codigoNulo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> carrito.aplicarCupon(null));
    }

    // ---------- R6: total ----------
    @Test
    @DisplayName("Total con envío pagado: 100.000 × 1,19 + 12.000 = 131.000")
    void calcularTotal_bajoMinimoEnvioGratis_sumaEnvio() {
        carrito.agregarProducto("Libro", 100_000, 1);
        assertEquals(131_000, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Total con envío gratis y SENA10: (500.000 − 50.000) × 1,19 = 535.500")
    void calcularTotal_conSena10EnvioGratis_535500() {
        carrito.agregarProducto("Monitor", 500_000, 1);
        carrito.aplicarCupon("SENA10");
        assertEquals(535_500, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Total con SENA10 y envío pagado: 90.000 × 1,19 + 12.000 = 119.100")
    void calcularTotal_conSena10EnvioPagado_119100() {
        carrito.agregarProducto("Libro", 100_000, 1);
        carrito.aplicarCupon("SENA10");
        assertEquals(119_100, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Total con BIENVENIDO: (60.000 − 5.000) × 1,19 + 12.000 = 77.450")
    void calcularTotal_conBienvenido_77450() {
        carrito.agregarProducto("Kit", 60_000, 1);
        carrito.aplicarCupon("BIENVENIDO");
        assertEquals(77_450, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Justo debajo del envío gratis: 126.000 × 1,19 = 149.940 → paga envío = 161.940")
    void calcularTotal_justoBajoEnvioGratis_pagaEnvio() {
        carrito.agregarProducto("Pack", 126_000, 1);
        assertEquals(161_940, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Justo sobre el envío gratis: 126.051 × 1,19 = 150.000,69 → sin envío = 150.001")
    void calcularTotal_justoSobreEnvioGratis_sinEnvio() {
        carrito.agregarProducto("Pack", 126_051, 1);
        assertEquals(150_001, carrito.calcularTotal(), 0.001);
    }
}
