package employeetimesheet.timesheet.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import employeetimesheet.timesheet.dto.TaskCategoryDTO;
import employeetimesheet.timesheet.entity.TaskCategory;
import employeetimesheet.timesheet.repository.TaskCategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskCategoryService {
    private final TaskCategoryRepository repository;

    public List<TaskCategory> findAll() { return repository.findAll(); }

    public TaskCategory findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
    }

    public TaskCategory create(TaskCategoryDTO dto) {
        TaskCategory c = TaskCategory.builder()
                .categoryName(dto.getCategoryName())
                .description(dto.getDescription())
                .build();
        return repository.save(c);
    }

    public TaskCategory update(Integer id, TaskCategoryDTO dto) {
        TaskCategory c = findById(id);
        c.setCategoryName(dto.getCategoryName());
        c.setDescription(dto.getDescription());
        return repository.save(c);
    }

    public void delete(Integer id) { repository.deleteById(id); }
}
