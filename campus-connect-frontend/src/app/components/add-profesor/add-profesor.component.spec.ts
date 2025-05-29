import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddProfesorComponent } from './add-profesor.component';

describe('AddProfesorComponent', () => {
  let component: AddProfesorComponent;
  let fixture: ComponentFixture<AddProfesorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddProfesorComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddProfesorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
