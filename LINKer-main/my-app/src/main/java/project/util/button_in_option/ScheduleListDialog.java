package project.util.button_in_option;

import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.*;
import java.util.regex.*;
import javax.swing.*;

public class ScheduleListDialog extends JDialog {
    private DefaultListModel<String> scheduleListModel;
    private JList<String> scheduleList;
    private static final String FILE_PATH = "schedules.txt";

    public ScheduleListDialog(JFrame parent) {
        super(parent, "일정 목록", true);
        setSize(450, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        Color darkBg = new Color(24, 26, 30);

        // ▶ 일정 리스트
        scheduleListModel = new DefaultListModel<>();
        scheduleList = new JList<>(scheduleListModel);
        scheduleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scheduleList.setBackground(darkBg); // 리스트 배경색
        scheduleList.setForeground(Color.WHITE); // 글자색 흰색

        scheduleList.setCellRenderer(new ListCellRenderer<String>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JTextArea textArea = new JTextArea(value);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.setFont(list.getFont());
                textArea.setOpaque(true);
                textArea.setEditable(false);
                textArea.setForeground(Color.WHITE);
                textArea.setBackground(isSelected ? new Color(50, 50, 50) : new Color(24, 26, 30));
                textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                // 리스트 너비에 맞게 텍스트 영역 크기 지정
                int width = list.getWidth();
                if (width > 0) {
                    textArea.setSize(width, Short.MAX_VALUE);
                }

                return textArea;
            }
        });

        JScrollPane scrollPane = new JScrollPane(scheduleList);
        scrollPane.getViewport().setBackground(darkBg); // 스크롤 안쪽 배경
        add(scrollPane, BorderLayout.CENTER);

        // ▶ 버튼 패널
        JButton addButton = new JButton("일정 등록");
        JButton editButton = new JButton("수정");
        JButton deleteButton = new JButton("삭제");

        addButton.addActionListener(e -> {
            ScheduleDialog dialog = new ScheduleDialog(parent, scheduleListModel);
            dialog.setVisible(true);
        });

        editButton.addActionListener(e -> {
            int selectedIndex = scheduleList.getSelectedIndex();
            if (selectedIndex != -1) {
                String selectedItem = scheduleListModel.getElementAt(selectedIndex);
                String regex = "\\[(.*?)\\] (.*?) - (.*)";
                Matcher matcher = Pattern.compile(regex).matcher(selectedItem);
                if (matcher.matches()) {
                    String dateStr = matcher.group(1);
                    String title = matcher.group(2);
                    String content = matcher.group(3);
                    ScheduleDialog dialog = new ScheduleDialog(parent, scheduleListModel, selectedIndex, dateStr, title,
                            content);
                    dialog.setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "수정할 일정을 선택하세요.");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedIndex = scheduleList.getSelectedIndex();
            if (selectedIndex != -1) {
                int confirm = JOptionPane.showConfirmDialog(this, "삭제하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    scheduleListModel.remove(selectedIndex);
                    saveSchedulesToFile(); // 삭제 후 저장
                }
            } else {
                JOptionPane.showMessageDialog(this, "삭제할 일정을 선택하세요.");
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(addButton);
        bottomPanel.add(editButton);
        bottomPanel.add(deleteButton);
        add(bottomPanel, BorderLayout.SOUTH);

        loadSchedulesFromFile();
    }

    // 일정 등록 및 수정 다이얼로그
    public class ScheduleDialog extends JDialog {
        private JTextField dateField;
        private JTextField titleField;
        private JTextArea contentArea;

        public ScheduleDialog(JFrame parent, DefaultListModel<String> model) {
            this(parent, model, -1, "", "", "");
        }

        public ScheduleDialog(JFrame parent, DefaultListModel<String> model, int index,
                              String dateStr, String titleText, String contentText) {
            super(parent, index == -1 ? "일정 등록" : "일정 수정", true);
            setSize(420, 320);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            Color darkBg = new Color(24, 26, 30);

            JPanel inputPanel = new JPanel(new GridBagLayout());
            inputPanel.setBackground(darkBg); // 입력 패널 배경
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;

            // 날짜 입력
            gbc.gridx = 0;
            gbc.gridy = 0;
            JLabel dateLabel = new JLabel("날짜 (yyyy-MM-dd):");
            dateLabel.setForeground(Color.WHITE); // 폰트 색상 흰색 설정
            inputPanel.add(dateLabel, gbc);

            gbc.gridx = 1;
            dateField = new JTextField(20);
            dateField.setForeground(Color.WHITE); // 입력 텍스트 색상 흰색
            dateField.setBackground(new Color(24, 26, 30)); // 배경
            dateField.setCaretColor(Color.WHITE); // 커서 색상 흰색
            inputPanel.add(dateField, gbc);

            // 제목 입력
            gbc.gridx = 0;
            gbc.gridy = 1;
            JLabel titleLabel = new JLabel("제목:");
            titleLabel.setForeground(Color.WHITE);
            inputPanel.add(titleLabel, gbc);

            gbc.gridx = 1;
            titleField = new JTextField(20);
            titleField.setForeground(Color.WHITE);
            titleField.setBackground(new Color(24, 26, 30));
            titleField.setCaretColor(Color.WHITE);
            inputPanel.add(titleField, gbc);

            // 내용 입력
            gbc.gridx = 0;
            gbc.gridy = 2;
            JLabel contentLabel = new JLabel("내용:");
            contentLabel.setForeground(Color.WHITE);
            inputPanel.add(contentLabel, gbc);

            gbc.gridx = 1;
            contentArea = new JTextArea(5, 20);
            contentArea.setLineWrap(true);
            contentArea.setWrapStyleWord(true);
            contentArea.setForeground(Color.WHITE);
            contentArea.setBackground(new Color(24, 26, 30));
            contentArea.setCaretColor(Color.WHITE);

            JScrollPane scrollPane = new JScrollPane(contentArea);
            scrollPane.getViewport().setBackground(new Color(24, 26, 30)); // 스크롤 내부 배경도 맞춤
            inputPanel.add(scrollPane, gbc);

            add(inputPanel, BorderLayout.CENTER);
            getContentPane().setBackground(darkBg); // 전체 배경

            // 버튼
            JPanel buttonPanel = new JPanel();
            buttonPanel.setBackground(darkBg); // 버튼 패널 배경
            JButton confirmButton = new JButton(index == -1 ? "등록" : "수정");
            JButton cancelButton = new JButton("취소");
            buttonPanel.add(confirmButton);
            buttonPanel.add(cancelButton);
            add(buttonPanel, BorderLayout.SOUTH);

            // 초기값 세팅
            dateField.setText(dateStr);
            titleField.setText(titleText);
            contentArea.setText(contentText);

            // 등록/수정 버튼 동작
            confirmButton.addActionListener(e -> {
                String date = dateField.getText().trim();
                String title = titleField.getText().trim();
                String content = contentArea.getText().trim();

                if (!isValidDate(date)) {
                    JOptionPane.showMessageDialog(this, "날짜 형식이 올바르지 않습니다. (예: 2025-06-01)");
                    return;
                }

                if (!title.isEmpty()) {
                    String formatted = String.format("[%s] %s - %s", date, title, content);
                    if (index == -1) {
                        model.addElement(formatted);
                        JOptionPane.showMessageDialog(this, "일정이 등록되었습니다!");
                    } else {
                        model.set(index, formatted);
                        JOptionPane.showMessageDialog(this, "일정이 수정되었습니다!");
                    }
                    saveSchedulesToFile(); // 등록 또는 수정 후 저장
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "제목을 입력해주세요.");
                }
            });

            cancelButton.addActionListener(e -> dispose());
        }

        // 날짜 형식 유효성 검사
        private boolean isValidDate(String dateStr) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                sdf.parse(dateStr);
                return true;
            } catch (ParseException e) {
                return false;
            }
        }
    }

    // 파일에서 일정 불러오기
    private void loadSchedulesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) { // BufferedReader로 파일 읽기
            String line;
            while ((line = reader.readLine()) != null) {
                scheduleListModel.addElement(line); // 파일을 한 줄 씩 읽어서 scheduleListModel에 추가
            }
        } catch (IOException e) {
            System.out.println("일정 파일이 없거나 읽는 중 오류 발생: " + e.getMessage());
        }
    }

    // 파일에 일정 저장
    private void saveSchedulesToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) { // BufferedWriter로 파일 쓰기
            for (int i = 0; i < scheduleListModel.size(); i++) {
                writer.write(scheduleListModel.get(i));
                writer.newLine(); // scheduleListModel에 저장된 모든 일정을 하나씩 파일에 씀
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
