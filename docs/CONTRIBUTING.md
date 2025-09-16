# Contributing Guide

Thank you for your interest in contributing to the Reactive Transactional Mobility Platform! This guide will help you get started with contributing to the project.

## Table of Contents

1. [Code of Conduct](#code-of-conduct)
2. [Getting Started](#getting-started)
3. [Development Setup](#development-setup)
4. [Contributing Process](#contributing-process)
5. [Coding Standards](#coding-standards)
6. [Testing Guidelines](#testing-guidelines)
7. [Documentation Standards](#documentation-standards)
8. [Pull Request Process](#pull-request-process)
9. [Issue Reporting](#issue-reporting)

## Code of Conduct

This project follows the [Contributor Covenant Code of Conduct](https://www.contributor-covenant.org/version/2/1/code_of_conduct/). By participating, you are expected to uphold this code. Please report unacceptable behavior to contact@rcimobility.com.

## Getting Started

### Prerequisites

- Java 17+
- Node.js 18+
- Docker & Docker Compose
- Git
- Your favorite IDE (IntelliJ IDEA, VS Code recommended)

### Fork and Clone

1. Fork the repository on GitHub
2. Clone your fork locally:
```bash
git clone https://github.com/YOUR_USERNAME/reactive-transactional.git
cd reactive-transactional
```

3. Add the upstream repository:
```bash
git remote add upstream https://github.com/original-org/reactive-transactional.git
```

## Development Setup

### Quick Start

```bash
# Start development environment
docker-compose -f docker-compose.dev.yml up -d

# Or run locally
cd server && ./mvnw spring-boot:run
cd front && npm start
```

### IDE Setup

#### IntelliJ IDEA

1. **Import Project**:
   - Open IntelliJ IDEA
   - Import the project as Maven project
   - Enable annotation processing

2. **Code Style**:
   - Go to File → Settings → Editor → Code Style
   - Import the provided `intellij-java-style.xml`

3. **Plugins**:
   - Lombok Plugin
   - Spring Boot Assistant
   - Docker Plugin
   - MapStruct Support

#### VS Code

1. **Extensions**:
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - ES7+ React/Redux/React-Native snippets
   - Prettier - Code formatter
   - ESLint

2. **Settings**:
```json
{
  "java.configuration.updateBuildConfiguration": "interactive",
  "java.format.settings.url": "./java-style.xml",
  "editor.formatOnSave": true,
  "editor.codeActionsOnSave": {
    "source.organizeImports": true,
    "source.fixAll.eslint": true
  }
}
```

## Contributing Process

### 1. Choose an Issue

- Look for issues labeled `good first issue` for beginners
- Check `help wanted` for issues needing contributors
- Comment on the issue to get it assigned to you

### 2. Create a Feature Branch

```bash
# Update your fork
git fetch upstream
git checkout main
git merge upstream/main

# Create feature branch
git checkout -b feature/your-feature-name
# or
git checkout -b bugfix/issue-number
```

### 3. Make Your Changes

- Follow the coding standards
- Write tests for new functionality
- Update documentation as needed
- Keep commits focused and atomic

### 4. Test Your Changes

```bash
# Run backend tests
cd server
./mvnw test

# Run frontend tests
cd front
npm test

# Run integration tests
docker-compose -f docker-compose.test.yml up --abort-on-container-exit
```

### 5. Commit Your Changes

Follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

```bash
git commit -m "feat: add real-time user tracking"
git commit -m "fix: resolve database connection timeout"
git commit -m "docs: update API documentation"
git commit -m "test: add integration tests for car service"
```

**Commit Types**:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

## Coding Standards

### Backend (Java/Spring Boot)

#### Code Style

- Use Google Java Style Guide
- Indent with 2 spaces
- Maximum line length: 100 characters
- Use meaningful variable and method names

#### Example:

```java
@RestController
@RequestMapping("/api/cars")
@Validated
public class CarController {

  private final CarService carService;

  public CarController(CarService carService) {
    this.carService = carService;
  }

  @GetMapping
  public Flux<Car> getAllCars() {
    return carService.findAll();
  }

  @PostMapping
  public Mono<Car> createCar(@Valid @RequestBody CreateCarRequest request) {
    return carService.create(request);
  }
}
```

#### Best Practices

- Use constructor injection over field injection
- Prefer immutable objects with Lombok `@Value`
- Use reactive types (Mono/Flux) consistently
- Handle errors with proper exception handling
- Add comprehensive JavaDoc for public methods

```java
/**
 * Creates a new car with the provided coordinates.
 *
 * @param request the car creation request containing coordinates
 * @return a Mono containing the created car
 * @throws ValidationException if coordinates are invalid
 */
public Mono<Car> createCar(CreateCarRequest request) {
  return validateCoordinates(request)
      .then(carRepository.save(mapToEntity(request)))
      .onErrorMap(DataIntegrityViolationException.class, 
          ex -> new BusinessException("Car already exists"));
}
```

### Frontend (React/TypeScript)

#### Code Style

- Use 2 spaces for indentation
- Use semicolons
- Use single quotes for strings
- Maximum line length: 80 characters

#### Component Structure

```javascript
import React, { useState, useEffect, useCallback } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import PropTypes from 'prop-types';

import { selectCarsData } from '../store/mobilitySlice';
import { Marker } from './Marker';

/**
 * Interactive map component for displaying real-time vehicle locations.
 */
function InteractiveMap({ width, height, onMapClick }) {
  const dispatch = useDispatch();
  const carsData = useSelector(selectCarsData);
  
  const [viewport, setViewport] = useState({
    latitude: 48.092971,
    longitude: 7.06064,
    zoom: 13,
  });

  const handleViewportChange = useCallback((newViewport) => {
    setViewport(newViewport);
  }, []);

  useEffect(() => {
    // Component logic here
  }, [carsData]);

  return (
    <div className="interactive-map">
      {/* JSX content */}
    </div>
  );
}

InteractiveMap.propTypes = {
  width: PropTypes.number.isRequired,
  height: PropTypes.number.isRequired,
  onMapClick: PropTypes.func,
};

InteractiveMap.defaultProps = {
  onMapClick: () => {},
};

export default InteractiveMap;
```

#### Best Practices

- Use functional components with hooks
- Memoize expensive calculations with `useMemo`
- Use `useCallback` for event handlers passed to child components
- Extract custom hooks for reusable logic
- Use TypeScript for type safety (when applicable)

### Database

#### Migration Scripts

- Use Flyway migration naming: `V1.0.0__Create_tables.sql`
- Always include rollback scripts
- Test migrations on sample data

#### Example Migration

```sql
-- V1.1.0__Add_car_status.sql
ALTER TABLE car ADD COLUMN status VARCHAR(20) DEFAULT 'ACTIVE';
CREATE INDEX idx_car_status ON car (status);

-- Add comment
COMMENT ON COLUMN car.status IS 'Current status of the car: ACTIVE, INACTIVE, MAINTENANCE';
```

## Testing Guidelines

### Backend Testing

#### Unit Tests

```java
@ExtendWith(MockitoExtension.class)
class CarServiceTest {

  @Mock
  private CarRepository carRepository;

  @InjectMocks
  private CarService carService;

  @Test
  @DisplayName("Should create car with valid coordinates")
  void shouldCreateCarWithValidCoordinates() {
    // Given
    CreateCarRequest request = CreateCarRequest.builder()
        .x(7.06064)
        .y(48.092971)
        .build();
    
    Car expectedCar = Car.builder()
        .id("test-id")
        .x(7.06064)
        .y(48.092971)
        .build();

    when(carRepository.save(any(Car.class)))
        .thenReturn(Mono.just(expectedCar));

    // When
    StepVerifier.create(carService.create(request))
        // Then
        .expectNext(expectedCar)
        .verifyComplete();
  }

  @Test
  @DisplayName("Should throw exception for invalid coordinates")
  void shouldThrowExceptionForInvalidCoordinates() {
    // Given
    CreateCarRequest request = CreateCarRequest.builder()
        .x(Double.NaN)
        .y(48.092971)
        .build();

    // When & Then
    StepVerifier.create(carService.create(request))
        .expectError(ValidationException.class)
        .verify();
  }
}
```

#### Integration Tests

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class CarControllerIntegrationTest {

  @Autowired
  private WebTestClient webTestClient;

  @Test
  void shouldCreateAndRetrieveCar() {
    CreateCarRequest request = CreateCarRequest.builder()
        .x(7.06064)
        .y(48.092971)
        .build();

    webTestClient.post()
        .uri("/cars")
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Car.class)
        .value(car -> {
          assertThat(car.getX()).isEqualTo(7.06064);
          assertThat(car.getY()).isEqualTo(48.092971);
          assertThat(car.getId()).isNotNull();
        });
  }
}
```

### Frontend Testing

#### Component Tests

```javascript
import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';

import { mobilitySlice } from '../store/mobilitySlice';
import InteractiveMap from './InteractiveMap';

const createTestStore = (initialState = {}) => {
  return configureStore({
    reducer: {
      mobility: mobilitySlice.reducer,
    },
    preloadedState: { mobility: initialState },
  });
};

describe('InteractiveMap', () => {
  it('renders without crashing', () => {
    const store = createTestStore();
    render(
      <Provider store={store}>
        <InteractiveMap width={800} height={600} />
      </Provider>
    );
    
    expect(screen.getByTestId('interactive-map')).toBeInTheDocument();
  });

  it('displays car markers when data is provided', () => {
    const carsData = { id: 'car-1', x: 7.06064, y: 48.092971 };
    const store = createTestStore({ carsData });
    
    render(
      <Provider store={store}>
        <InteractiveMap width={800} height={600} />
      </Provider>
    );
    
    expect(screen.getByTestId('car-marker')).toBeInTheDocument();
  });
});
```

#### Redux Tests

```javascript
import { configureStore } from '@reduxjs/toolkit';
import { mobilitySlice, updateCars, updateUsers } from './mobilitySlice';

describe('mobilitySlice', () => {
  let store;

  beforeEach(() => {
    store = configureStore({
      reducer: { mobility: mobilitySlice.reducer },
    });
  });

  it('should update cars data', () => {
    const carData = { id: 'car-1', x: 7.06064, y: 48.092971 };
    
    store.dispatch(updateCars(carData));
    
    const state = store.getState().mobility;
    expect(state.carsData).toEqual(carData);
    expect(state.loading).toBe(false);
  });
});
```

### Test Coverage Requirements

- **Backend**: Minimum 80% line coverage
- **Frontend**: Minimum 70% line coverage
- **Critical paths**: 100% coverage required

## Documentation Standards

### Code Documentation

#### JavaDoc for Java

```java
/**
 * Service for managing car entities and their real-time location updates.
 * 
 * <p>This service provides reactive operations for creating, updating, and
 * querying car locations. All operations are non-blocking and return
 * reactive types.
 *
 * @author John Doe
 * @since 1.0.0
 */
@Service
public class CarService {

  /**
   * Creates a new car with the specified coordinates.
   *
   * @param request the car creation request containing coordinates
   * @return a {@link Mono} containing the created car
   * @throws ValidationException if the coordinates are invalid
   * @throws BusinessException if a car with the same ID already exists
   */
  public Mono<Car> create(CreateCarRequest request) {
    // Implementation
  }
}
```

#### JSDoc for JavaScript

```javascript
/**
 * Interactive map component for displaying real-time vehicle tracking.
 * 
 * @component
 * @param {Object} props - Component props
 * @param {number} props.width - Map width in pixels
 * @param {number} props.height - Map height in pixels
 * @param {Function} [props.onMapClick] - Callback for map click events
 * @returns {JSX.Element} The rendered map component
 * 
 * @example
 * <InteractiveMap 
 *   width={800} 
 *   height={600} 
 *   onMapClick={(coords) => console.log(coords)}
 * />
 */
function InteractiveMap({ width, height, onMapClick = () => {} }) {
  // Component implementation
}
```

### API Documentation

Use OpenAPI annotations:

```java
@Operation(
    summary = "Create a new car",
    description = "Creates a new car entity with the provided coordinates",
    responses = {
        @ApiResponse(responseCode = "200", description = "Car created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    }
)
@PostMapping
public Mono<Car> createCar(
    @Parameter(description = "Car creation request", required = true)
    @Valid @RequestBody CreateCarRequest request) {
  return carService.create(request);
}
```

## Pull Request Process

### Before Submitting

1. **Update your branch**:
```bash
git fetch upstream
git rebase upstream/main
```

2. **Run all tests**:
```bash
# Backend tests
cd server && ./mvnw verify

# Frontend tests  
cd front && npm test -- --coverage --watchAll=false

# Integration tests
docker-compose -f docker-compose.test.yml up --abort-on-container-exit
```

3. **Check code quality**:
```bash
# Java code quality
./mvnw spotbugs:check pmd:check checkstyle:check

# JavaScript/React code quality
npm run lint
npm run type-check
```

### Pull Request Template

```markdown
## Description
Brief description of changes made.

## Type of Change
- [ ] Bug fix (non-breaking change which fixes an issue)
- [ ] New feature (non-breaking change which adds functionality)
- [ ] Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] Documentation update

## Testing
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] Manual testing completed
- [ ] Performance impact assessed

## Checklist
- [ ] My code follows the project's coding standards
- [ ] I have performed a self-review of my code
- [ ] I have commented my code, particularly in hard-to-understand areas
- [ ] I have made corresponding changes to the documentation
- [ ] My changes generate no new warnings
- [ ] I have added tests that prove my fix is effective or that my feature works
- [ ] New and existing unit tests pass locally with my changes

## Screenshots (if applicable)
Add screenshots to help explain your changes.

## Additional Notes
Any additional information or context about the changes.
```

### Review Process

1. **Automated Checks**: CI/CD pipeline runs tests and quality checks
2. **Code Review**: At least one maintainer reviews the code
3. **Testing**: Reviewer tests the functionality
4. **Approval**: Changes are approved and merged

## Issue Reporting

### Bug Reports

Use the bug report template:

```markdown
**Bug Description**
A clear and concise description of what the bug is.

**To Reproduce**
Steps to reproduce the behavior:
1. Go to '...'
2. Click on '....'
3. Scroll down to '....'
4. See error

**Expected Behavior**
A clear and concise description of what you expected to happen.

**Screenshots**
If applicable, add screenshots to help explain your problem.

**Environment:**
- OS: [e.g. Ubuntu 20.04]
- Browser [e.g. chrome, safari]
- Version [e.g. 22]
- Docker version [e.g. 20.10.8]

**Additional Context**
Add any other context about the problem here.
```

### Feature Requests

Use the feature request template:

```markdown
**Is your feature request related to a problem? Please describe.**
A clear and concise description of what the problem is.

**Describe the solution you'd like**
A clear and concise description of what you want to happen.

**Describe alternatives you've considered**
A clear and concise description of any alternative solutions or features you've considered.

**Additional context**
Add any other context or screenshots about the feature request here.
```

## Getting Help

- **Discord**: Join our Discord server for real-time help
- **GitHub Discussions**: For general questions and discussions
- **Stack Overflow**: Tag questions with `reactive-transactional`
- **Email**: contact@rcimobility.com for sensitive issues

## Recognition

Contributors will be recognized in:
- CONTRIBUTORS.md file
- Release notes
- Project README
- Annual contributor appreciation post

Thank you for contributing to the Reactive Transactional Mobility Platform! 🚀