package views;

import model.Dispositivo;
import services.ScannerRedLogic;
import utils.ValidacionesIP;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class InterfazScanner extends JFrame {

    private JTextField txtIpInicio, txtIpFin, txtTimeout, txtReintentos;
    private JTable tablaResultados;
    private DefaultTableModel modeloTabla;
    private JProgressBar barraProgreso;
    private JLabel lblEstado, lblEquiposActivos;
    private JButton btnIniciar, btnDetener, btnLimpiar, btnGuardar, btnFiltrarActivos;

    private SwingWorker<Void, Dispositivo> workerEscaneo;
    private List<Dispositivo> listaCompleta = new ArrayList<>();
    private boolean mostrandoSoloActivos = false;
    private int contadorActivos = 0;

    public InterfazScanner() {
        setTitle("Escáner de Red");
        setSize(850, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel panelIzquierdo = new JPanel(new BorderLayout(5, 5));

        JPanel panelConfig = new JPanel(new GridLayout(8, 1, 5, 5));
        panelConfig.setBorder(BorderFactory.createTitledBorder("Configuración"));

        panelConfig.add(new JLabel("IP de inicio:"));
        txtIpInicio = new JTextField("10.160.7.223");
        panelConfig.add(txtIpInicio);

        panelConfig.add(new JLabel("IP de fin:"));
        txtIpFin = new JTextField("10.160.7.233");
        panelConfig.add(txtIpFin);

        panelConfig.add(new JLabel("Tiempo de espera (ms):"));
        txtTimeout = new JTextField("1000");
        panelConfig.add(txtTimeout);

        panelConfig.add(new JLabel("Reintentos:"));
        txtReintentos = new JTextField("1");
        panelConfig.add(txtReintentos);

        JPanel panelBotones = new JPanel(new GridLayout(5, 1, 5, 5));
        panelBotones.setBorder(BorderFactory.createTitledBorder("Acciones"));

        btnIniciar = new JButton("Iniciar escaneo");
        btnDetener = new JButton("Detener escaneo");
        btnLimpiar = new JButton("Limpiar");
        btnGuardar = new JButton("Guardar resultados");
        btnFiltrarActivos = new JButton("Mostrar solo activos");

        btnDetener.setEnabled(false);

        panelBotones.add(btnIniciar);
        panelBotones.add(btnDetener);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnFiltrarActivos);
        panelBotones.add(btnGuardar);

        panelIzquierdo.add(panelConfig, BorderLayout.NORTH);
        panelIzquierdo.add(panelBotones, BorderLayout.SOUTH);

        add(panelIzquierdo, BorderLayout.WEST);

        JPanel panelDerecho = new JPanel(new BorderLayout(5, 5));

        JPanel panelEstado = new JPanel(new GridLayout(2, 1, 5, 5));
        panelEstado.setBorder(BorderFactory.createTitledBorder("Estado"));

        lblEstado = new JLabel("Estado: Esperando...", SwingConstants.CENTER);
        lblEquiposActivos = new JLabel("Equipos activos: 0", SwingConstants.CENTER);
        barraProgreso = new JProgressBar();
        barraProgreso.setStringPainted(true);

        JPanel panelInfo = new JPanel(new GridLayout(1, 2));
        panelInfo.add(lblEstado);
        panelInfo.add(lblEquiposActivos);

        panelEstado.add(panelInfo);
        panelEstado.add(barraProgreso);

        panelDerecho.add(panelEstado, BorderLayout.NORTH);

        String[] columnas = {"IP", "Nombre equipo", "Activo", "Tiempo (ms)"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) return Boolean.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaResultados = new JTable(modeloTabla);
        panelDerecho.add(new JScrollPane(tablaResultados), BorderLayout.CENTER);

        add(panelDerecho, BorderLayout.CENTER);

        btnIniciar.addActionListener(e -> iniciarEscaneo());
        btnDetener.addActionListener(e -> detenerEscaneo());
        btnLimpiar.addActionListener(e -> limpiarInterfaz());
        btnGuardar.addActionListener(e -> guardarResultados());
        btnFiltrarActivos.addActionListener(e -> alternarFiltroActivos());
    }

    private void iniciarEscaneo() {
        String ipInicioStr = txtIpInicio.getText().trim();
        String ipFinStr = txtIpFin.getText().trim();

        if (!ValidacionesIP.esIPv4Valida(ipInicioStr) || !ValidacionesIP.esIPv4Valida(ipFinStr)) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese direcciones IPv4 válidas.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            long ipInicio = ValidacionesIP.ipToLong(ipInicioStr);
            long ipFin = ValidacionesIP.ipToLong(ipFinStr);

            if (ipInicio > ipFin) {
                JOptionPane.showMessageDialog(this, "La IP de inicio no puede ser mayor que la IP de fin.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int timeout = Integer.parseInt(txtTimeout.getText().trim());
            int reintentos = Integer.parseInt(txtReintentos.getText().trim());

            limpiarInterfaz();
            btnIniciar.setEnabled(false);
            btnDetener.setEnabled(true);

            int totalIPs = (int) (ipFin - ipInicio + 1);
            barraProgreso.setMaximum(totalIPs);
            barraProgreso.setValue(0);

            workerEscaneo = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    ScannerRedLogic scanner = new ScannerRedLogic();
                    int procesadas = 0;

                    for (long ipActual = ipInicio; ipActual <= ipFin && !isCancelled(); ipActual++) {
                        String ipStr = ValidacionesIP.longToIp(ipActual);
                        Dispositivo disp = scanner.escanearIP(ipStr, timeout, reintentos);

                        procesadas++;
                        setProgress(procesadas);
                        publish(disp);
                    }
                    return null;
                }

                @Override
                protected void process(List<Dispositivo> chunks) {
                    for (Dispositivo disp : chunks) {
                        listaCompleta.add(disp);
                        barraProgreso.setValue(barraProgreso.getValue() + 1);

                        if (disp.isActivo()) {
                            contadorActivos++;
                            lblEquiposActivos.setText("Equipos activos: " + contadorActivos);
                        }

                        if (!mostrandoSoloActivos || disp.isActivo()) {
                            agregarFilaTabla(disp);
                        }
                    }
                }

                @Override
                protected void done() {
                    btnIniciar.setEnabled(true);
                    btnDetener.setEnabled(false);
                    lblEstado.setText(isCancelled() ? "Escaneo cancelado" : "Escaneo finalizado");
                }
            };

            lblEstado.setText("Escaneando...");
            workerEscaneo.execute();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El timeout y reintentos deben ser números enteros.", "Error de entrada", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al iniciar el escaneo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void detenerEscaneo() {
        if (workerEscaneo != null && !workerEscaneo.isDone()) {
            workerEscaneo.cancel(true);
        }
    }

    private void limpiarInterfaz() {
        modeloTabla.setRowCount(0);
        listaCompleta.clear();
        contadorActivos = 0;
        lblEquiposActivos.setText("Equipos activos: 0");
        barraProgreso.setValue(0);
        lblEstado.setText("Estado: Esperando...");
    }

    private void agregarFilaTabla(Dispositivo disp) {
        modeloTabla.addRow(new Object[]{
                disp.getIp(),
                disp.getNombreEquipo(),
                disp.isActivo(),
                disp.isActivo() ? disp.getTiempoRespuesta() : ""
        });
    }

    private void guardarResultados() {
        if (listaCompleta.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay datos para guardar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(fileChooser.getSelectedFile() + ".csv"))) {
                pw.println("IP,Nombre Equipo,Activo,Tiempo (ms)");
                for (Dispositivo d : listaCompleta) {
                    pw.printf("%s,%s,%s,%s\n",
                            d.getIp(),
                            d.getNombreEquipo(),
                            d.isActivo() ? "Sí" : "No",
                            d.isActivo() ? d.getTiempoRespuesta() : "");
                }
                JOptionPane.showMessageDialog(this, "Resultados guardados con éxito en archivo CSV.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void alternarFiltroActivos() {
        mostrandoSoloActivos = !mostrandoSoloActivos;
        btnFiltrarActivos.setText(mostrandoSoloActivos ? "Mostrar todos" : "Mostrar solo activos");

        modeloTabla.setRowCount(0);
        for (Dispositivo d : listaCompleta) {
            if (!mostrandoSoloActivos || d.isActivo()) {
                agregarFilaTabla(d);
            }
        }
    }
} //Fin de la interfaz