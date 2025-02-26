package com.iesvdc.pizza.controller;

import com.iesvdc.pizza.entity.Pedido;
import com.iesvdc.pizza.entity.UserInfo;
import com.iesvdc.pizza.repository.UserInfoRepository;
import com.iesvdc.pizza.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para gestionar los pedidos en la aplicación PomPizza.
 * Proporciona endpoints para crear, consultar, actualizar y eliminar pedidos.
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UserInfoRepository userRepository;

    /**
     * Crea un nuevo pedido. Solo los usuarios con rol CLIENTE pueden acceder a este endpoint.
     * @param pedido el pedido a crear
     * @return el pedido creado envuelto en un ResponseEntity
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    public ResponseEntity<Pedido> crearPedido(@RequestBody Pedido pedido) {
        Pedido nuevoPedido = pedidoService.guardarPedido(pedido);
        return ResponseEntity.ok(nuevoPedido);
    }

    /**
     * Obtiene todos los pedidos registrados en la base de datos. Solo accesible para usuarios con rol ADMIN.
     * @return una lista de todos los pedidos
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Pedido>> obtenerTodosLosPedidos() {
        List<Pedido> pedidos = pedidoService.obtenerTodosLosPedidos();
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Obtiene los pedidos realizados por el cliente autenticado.
     * @param userDetails los detalles del usuario autenticado
     * @return una lista de los pedidos realizados por el cliente
     */
    @GetMapping("/misPedidos")
    public ResponseEntity<List<Pedido>> obtenerMisPedidos(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<Pedido> pedidos = pedidoService.obtenerPedidosPorCliente(username);
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Obtiene un pedido por su ID. Accesible para usuarios con rol CLIENTE o ADMIN.
     * @param id el identificador del pedido
     * @return el pedido encontrado o un ResponseEntity con estado 404 si no se encuentra
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE', 'ROLE_ADMIN')")
    public ResponseEntity<Pedido> obtenerPedidoPorId(@PathVariable String id) {
        Optional<Pedido> pedido = pedidoService.obtenerPedidoPorId(id);
        return pedido.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Actualiza el estado de un pedido existente. Solo accesible para usuarios con rol ADMIN.
     * @param id el identificador del pedido a actualizar
     * @param pedido el pedido con el nuevo estado
     * @return el pedido actualizado envuelto en un ResponseEntity, o 404 si no se encuentra
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Pedido> actualizarEstadoPedido(@PathVariable String id, @RequestBody Pedido pedido) {
        Optional<Pedido> pedidoExistente = pedidoService.obtenerPedidoPorId(id);

        if (pedidoExistente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Pedido pedidoActualizado = pedidoExistente.get();
        pedidoActualizado.setEstado(pedido.getEstado());

        Pedido pedidoGuardado = pedidoService.guardarPedido(pedidoActualizado);

        return ResponseEntity.ok(pedidoGuardado);
    }

    /**
     * Elimina un pedido por su ID. Solo accesible para usuarios con rol ADMIN.
     * @param id el identificador del pedido a eliminar
     * @return un ResponseEntity con estado 204 (sin contenido) si la eliminación es exitosa
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> eliminarPedido(@PathVariable String id) {
        pedidoService.eliminarPedido(id);
        return ResponseEntity.noContent().build();
    }
}
