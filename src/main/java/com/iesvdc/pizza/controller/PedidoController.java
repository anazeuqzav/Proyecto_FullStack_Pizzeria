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

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UserInfoRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    public ResponseEntity<Pedido> crearPedido(@RequestBody Pedido pedido) {
        Pedido nuevoPedido = pedidoService.guardarPedido(pedido);
        return ResponseEntity.ok(nuevoPedido);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Pedido>> obtenerTodosLosPedidos() {
        List<Pedido> pedidos = pedidoService.obtenerTodosLosPedidos();
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Método que  obtiene los pedidos de un cliente en particular
     * @param userDetails los detalles del usuario (cliente)
     * @return una lista con los pedidos que ha realizado el cliente
     */
    @GetMapping("/misPedidos")
    public ResponseEntity<List<Pedido>> obtenerMisPedidos(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<Pedido> pedidos = pedidoService.obtenerPedidosPorCliente(username); // Buscar por username directamente
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE', 'ROLE_ADMIN')")
    public ResponseEntity<Pedido> obtenerPedidoPorId(@PathVariable String id) {
        Optional<Pedido> pedido = pedidoService.obtenerPedidoPorId(id);
        return pedido.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Actualizar estado del pedido - Solo ADMIN
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Pedido> actualizarEstadoPedido(@PathVariable String id, @RequestBody Pedido pedido) {
        Optional<Pedido> pedidoExistente = pedidoService.obtenerPedidoPorId(id);

        if (pedidoExistente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Pedido pedidoActualizado = pedidoExistente.get();
        pedidoActualizado.setEstado(pedido.getEstado()); // Solo actualiza el estado

        // Guarda el pedido con los demás datos intactos
        Pedido pedidoGuardado = pedidoService.guardarPedido(pedidoActualizado);

        return ResponseEntity.ok(pedidoGuardado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> eliminarPedido(@PathVariable String id) {
        pedidoService.eliminarPedido(id);
        return ResponseEntity.noContent().build();
    }
}