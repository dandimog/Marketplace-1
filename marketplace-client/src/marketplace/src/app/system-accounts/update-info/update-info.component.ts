import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  AbstractControl,
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import { Role } from '../../_models/role';
import { SystemAccountService } from '../../_services/system-account.service';
import { validateBirthday } from '../../_helpers/validators.service';
import { StaffMember } from '../../_models/staff-member';

import { first } from 'rxjs/operators';

@Component({
  selector: 'update-info',
  templateUrl: './update-info.component.html',
  styleUrls: ['./update-info.component.css'],
})
export class UpdateInfoComponent implements OnInit {
  //form: FormGroup;
  form = new FormGroup({
    name: new FormControl(''),
  });

  submitted = false;

  courierStatuses: string[] = ['active', 'inactive', 'terminated'];

  pmStatuses: string[] = ['active', 'terminated'];

  loading = false;

  updated = false;

  response: any;

  ngOnInit() {
    //.subscribe((response) => {
    if (this.route.snapshot.params.role.localeCompare(1)) {
      this.accountService
        .getManagerProfileInfo(this.route.snapshot.params.id)
        .subscribe((response) => {
          this.response = response;
          this.formCreation();
        });
    } else if (this.route.snapshot.params.role.localeCompare(2)) {
      this.accountService
        .getCourierProfileInfo(this.route.snapshot.params.id)
        .subscribe((response) => {
          this.response = response;
          this.formCreation();
        });
    }
  }

  constructor(
    private formBuilder: FormBuilder,
    private accountService: SystemAccountService,
    private route: ActivatedRoute
  ) {}

  formCreation() {
    this.form = this.formBuilder.group(
      {
        name: [this.response.name, Validators.required],
        surname: [this.response.surname, Validators.required],
        phone: [this.response.phone, Validators.pattern(/\+380[0-9]{9}/)],
        birthday: [this.response.dateOfBirth, Validators.required],
        status: [this.response.status, Validators.required],
      },
      {
        validator: [validateBirthday],
      }
    );
  }

  get getForm(): { [p: string]: AbstractControl } {
    return this.form.controls;
  }

  isProductManager(): boolean {
    return this.response.role === Role.ProductManager;
  }

  isCourier(): boolean {
    return this.response.role === Role.Courier;
  }

  private mapToStaffMember(o: any): StaffMember {
    return {
      id: -1,
      name: o.name,
      surname: o.surname,
      email: this.response.email,
      dateOfBirth: o.birthday,
      phone: o.phone,
      role: this.response.role,
      status: o.status,
    };
  }

  onSubmit(): void {
    this.submitted = true;
    if (this.form.invalid) {
      return;
    }
    this.form.disable();
    this.loading = true;
    let observable = null;
    if (this.route.snapshot.params.role.localeCompare(1)) {
      observable = this.accountService.updateManagerInfo(
        this.mapToStaffMember(this.form.value),
        this.route.snapshot.params.id
      );
    } else if (this.route.snapshot.params.role.localeCompare(2)) {
      observable = this.accountService.updateCourierInfo(
        this.mapToStaffMember(this.form.value),
        this.route.snapshot.params.id
      );
    } else {
      this.form.enable();
      return;
    }

    observable.pipe(first()).subscribe({
      next: () => {
        this.loading = false;
        this.updated = true;
        this.form.enable();
      },
    });
  }
}
